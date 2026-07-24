package com.tocktalks.domain.price.service;

import com.tocktalks.domain.price.config.KisApiProperties;
import com.tocktalks.domain.price.dto.request.KisRealtimeSubscribeRequest;
import com.tocktalks.domain.price.dto.response.KisRealtimePriceMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.PongMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class KisWebSocketClient extends TextWebSocketHandler {

    private static final String TR_ID_CCNL_KRX = "H0STCNT0";
    private static final String WS_PATH = "/tryitout";

    private final KisApiProperties kisApiProperties;
    private final KisAuthService kisAuthService;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final PricePublisher pricePublisher;
    private final Set<String> subscribedStockCodes = ConcurrentHashMap.newKeySet();

    private WebSocketSession session;
    private long reconnectDelaySeconds = 1;
    private volatile long connectedAtMillis = 0L;
    // ALREADY IN USE 등으로 KIS가 즉시 거부하는 경우, WebSocket 핸드셰이크 자체는 성공하고
    // 곧바로 세션이 닫힌다. 이 시간보다 짧게 유지된 연결은 "성공한 연결"로 치지 않고
    // 백오프를 그대로 키운다 — 안 그러면 매번 핸드셰이크 성공 시점에 딜레이가 초기화돼서
    // 초당 재시도로 KIS를 계속 두드리게 된다.
    private static final long MIN_STABLE_CONNECTION_MS = 5000L;

    public KisWebSocketClient(KisApiProperties kisApiProperties,
                              KisAuthService kisAuthService,
                              StringRedisTemplate redisTemplate,
                              ObjectMapper objectMapper,
                              PricePublisher pricePublisher) {
        this.kisApiProperties = kisApiProperties;
        this.kisAuthService = kisAuthService;
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
        this.pricePublisher = pricePublisher;
    }

    public synchronized void connect() {
        if (session != null && session.isOpen()) {
            return;
        }
        try {
            WebSocketClient client = new StandardWebSocketClient();
            String url = kisApiProperties.websocketUrl() + WS_PATH;
            this.session = client.execute(this, url).get();
            connectedAtMillis = System.currentTimeMillis();
        } catch (Exception e) {
            throw new IllegalStateException("KIS WebSocket 연결 실패", e);
        }
    }

    public void subscribe(String stockCode) throws IOException {
        sendSubscribeRequest(stockCode);
        subscribedStockCodes.add(stockCode);
    }

    private void sendSubscribeRequest(String stockCode) throws IOException {
        if (!isSessionOpen()) {
            // 세션이 아직(혹은 더 이상) 없어도 subscribedStockCodes엔 그대로 남아있으니,
            // 다음 연결 성공 시 resubscribeAll()이 알아서 다시 요청해준다.
            log.debug("[KIS 구독 건너뜀] 세션 없음 stockCode={}", stockCode);
            return;
        }

        String approvalKey = kisAuthService.getApprovalKey();
        KisRealtimeSubscribeRequest request = KisRealtimeSubscribeRequest.of(approvalKey, TR_ID_CCNL_KRX, stockCode);

        String json = objectMapper.writeValueAsString(request);
        sendMessageSafely(stockCode, json, "구독");
    }

    // isSessionOpen() 체크와 실제 sendMessage() 호출 사이에도 세션이 닫힐 수 있다
    // (KIS가 ALREADY IN USE 등으로 응답하면서 세션을 바로 닫는 경우 특히 잦다).
    // 그 레이스를 완전히 없앨 수는 없으니, 닫힌 세션에 보내다 나는 예외는 재연결로
    // 알아서 복구되는 일시적 상황으로 보고 조용히 넘어간다.
    private void sendMessageSafely(String stockCode, String json, String action) throws IOException {
        try {
            session.sendMessage(new TextMessage(json));
        } catch (IllegalStateException e) {
            log.debug("[KIS {} 건너뜀] 전송 시점에 세션이 이미 닫힘 stockCode={}", action, stockCode);
        }
    }

    private boolean isSessionOpen() {
        return session != null && session.isOpen();
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        String payload = message.getPayload();
        log.debug("[KIS 수신] {}", payload);

        if (payload.startsWith("0") || payload.startsWith("1")) {
            handleRealtimeData(payload);
        } else {
            handlePingPong(payload, session);
        }
    }

    private void handleRealtimeData(String payload) {
        String[] parts = payload.split("\\|");
        if  (parts.length < 4) {
            return;
        }

        String trId = parts[1];
        if (TR_ID_CCNL_KRX.equals(trId)) {
            try {
                KisRealtimePriceMessage priceMessage = KisRealtimePriceMessage.from(parts[3]);
                log.debug("[파싱 완료] {} = {}", priceMessage.stockCode(), priceMessage.currentPrice());

                persistLatestPriceThrottled(priceMessage.stockCode(), priceMessage.currentPrice());
                pricePublisher.publish(priceMessage.stockCode(), priceMessage.currentPrice());

                log.debug("[발행 완료]");
            } catch (Exception e) {
                log.error("[handleRealtimeData 예외 발생]", e);
            }
        }
    }

    // KIS 틱은 종목당 초당 여러 번 오는데, price:latest:*는 배치조회(price:quote:*)까지
    // 실패했을 때만 쓰는 최후 폴백이라 틱마다 Redis에 쓸 필요가 없다. 틱마다 SET+PUBLISH를
    // 다 걸면 shared(원격 Upstash) 환경에서 명령 수가 순식간에 불어난다.
    private static final long LATEST_PRICE_WRITE_INTERVAL_MS = 5000;
    private final Map<String, Long> lastLatestPriceWriteAt = new ConcurrentHashMap<>();

    private void persistLatestPriceThrottled(String stockCode, String currentPrice) {
        long now = System.currentTimeMillis();
        Long lastWriteAt = lastLatestPriceWriteAt.get(stockCode);
        if (lastWriteAt != null && now - lastWriteAt < LATEST_PRICE_WRITE_INTERVAL_MS) {
            return;
        }
        lastLatestPriceWriteAt.put(stockCode, now);
        redisTemplate.opsForValue().set("price:latest:" + stockCode, currentPrice);
    }

    // KIS 서버와의 네트워크 연결이 유지되고 있는지 확인하기 위한 용도
    private void handlePingPong(String payload, WebSocketSession session) throws IOException {
        JsonNode root = objectMapper.readTree(payload);
        String trId = root.path("header").path("tr_id").asText();

        if ("PINGPONG".equals(trId)) {
            session.sendMessage(new PongMessage(ByteBuffer.wrap(payload.getBytes(StandardCharsets.UTF_8))));
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        boolean wasStableConnection =
                System.currentTimeMillis() - connectedAtMillis >= MIN_STABLE_CONNECTION_MS;
        reconnectDelaySeconds = wasStableConnection ? 1 : Math.min(reconnectDelaySeconds * 2, 60);
        scheduler.schedule(this::reconnect, reconnectDelaySeconds, TimeUnit.SECONDS);
    }

    private void reconnect() {
        try {
            connect();
            resubscribeAll();
        } catch (Exception e) {
            reconnectDelaySeconds = Math.min(reconnectDelaySeconds * 2, 60);
            scheduler.schedule(this::reconnect, reconnectDelaySeconds, TimeUnit.SECONDS);
        }
    }

    private void resubscribeAll() {
        for (String stockCode : subscribedStockCodes) {
            try {
                sendSubscribeRequest(stockCode);
            } catch (IOException e) {
                log.warn("[resubscribeAll 실패] stockCode={}", stockCode, e);
            }
        }
    }

    public void unsubscribe(String stockCode) throws IOException {
        // 로컬 구독 목록은 세션 상태와 무관하게 항상 정리한다.
        subscribedStockCodes.remove(stockCode);

        if (!isSessionOpen()) {
            log.debug("[KIS 구독해제 건너뜀] 세션 없음 stockCode={}", stockCode);
            return;
        }

        String approvalKey = kisAuthService.getApprovalKey();
        KisRealtimeSubscribeRequest request =
                KisRealtimeSubscribeRequest.ofUnsubscribe(approvalKey, TR_ID_CCNL_KRX, stockCode);

        String json = objectMapper.writeValueAsString(request);
        sendMessageSafely(stockCode, json, "구독해제");
    }

}
