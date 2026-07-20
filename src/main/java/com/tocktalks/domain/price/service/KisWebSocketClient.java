package com.tocktalks.domain.price.service;

import com.tocktalks.domain.price.config.KisApiProperties;
import com.tocktalks.domain.price.dto.request.KisRealtimeSubscribeRequest;
import com.tocktalks.domain.price.dto.response.KisRealtimePriceMessage;
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
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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

    private WebSocketSession session;
    private long reconnectDelaySeconds = 1;

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

    public void connect() {
        try {
            WebSocketClient client = new StandardWebSocketClient();
            String url = kisApiProperties.websocketUrl() + WS_PATH;
            this.session = client.execute(this, url).get();
            reconnectDelaySeconds = 1;
        } catch (Exception e) {
            throw new IllegalStateException("KIS WebSocket 연결 실패", e);
        }
    }

    public void subscribe(String stockCode) throws IOException {
        String approvalKey = kisAuthService.getApprovalKey();
        KisRealtimeSubscribeRequest request = KisRealtimeSubscribeRequest.of(approvalKey, TR_ID_CCNL_KRX, stockCode);

        String json = objectMapper.writeValueAsString(request);
        session.sendMessage(new TextMessage(json));
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        String payload = message.getPayload();
        System.out.println("[KIS 수신] " + payload);   // 임시 디버깅용

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
                System.out.println("[파싱 완료] " + priceMessage.stockCode() + " = " + priceMessage.currentPrice());

                redisTemplate.opsForValue().set("price:latest:" + priceMessage.stockCode(), priceMessage.currentPrice());
                pricePublisher.publish(priceMessage.stockCode(), priceMessage.currentPrice());

                System.out.println("[발행 완료]");
            } catch (Exception e) {
                System.out.println("[handleRealtimeData 예외 발생]");
                e.printStackTrace();
            }
        }
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
        scheduler.schedule(this::reconnect, reconnectDelaySeconds, TimeUnit.SECONDS);
    }

    private void reconnect() {
        try {
            connect();
        } catch (Exception e) {
            reconnectDelaySeconds = Math.min(reconnectDelaySeconds * 2, 60);
            scheduler.schedule(this::reconnect, reconnectDelaySeconds, TimeUnit.SECONDS);
        }
    }

}
