package com.tocktalks.domain.price.service;

import com.tocktalks.domain.price.config.KisApiProperties;
import com.tocktalks.domain.price.dto.response.KisMultiPriceEnvelope;
import com.tocktalks.domain.price.dto.response.KisPriceEnvelope;
import com.tocktalks.domain.price.dto.response.KisPriceResponse;
import com.tocktalks.domain.price.dto.response.StockQuoteResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class KisPriceService {
    private static final String TR_ID_INQUIRE_PRICE = "FHKST01010100";
    private static final String MULTI_TR_ID = "FHKST11300006";
    private static final int MULTI_PRICE_MAX_CODES = 30;
    private static final String CACHE_KEY_PREFIX = "price:rest:";
    private static final String QUOTE_CACHE_KEY_PREFIX = "price:quote:";
    // 8초 캐시(CACHE_KEY_PREFIX)와 별개로, KIS 호출이 실패했을 때 화면에 아무 값도 못
    // 띄우는 대신 쓸 마지막 성공 응답을 TTL 없이(만료 없이) 보관한다.
    private static final String LATEST_KEY_PREFIX = "price:rest:latest:";
    // price:rest:*는 매수/매도 체결가로도 쓰이므로(BuyTradeService/SellTradeService) 짧게 유지한다.
    private static final Duration CACHE_TTL = Duration.ofSeconds(8);
    // price:quote:*는 포트폴리오 조회/워밍업/랭킹 스케줄러 전용 캐시라 체결가에 영향이 없다.
    // 랭킹 스케줄러 주기(30초)와 맞춰서, 스케줄러가 돌 때 캐시가 거의 항상 살아있게 한다.
    private static final Duration QUOTE_CACHE_TTL = Duration.ofSeconds(30);

    private final WebClient kisWebClient;
    private final KisApiProperties kisApiProperties;
    private final KisAuthService kisAuthService;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final KisRateLimiter kisRateLimiter;

    public KisPriceService(WebClient kisWebClient,
                           KisApiProperties kisApiProperties,
                           KisAuthService kisAuthService,
                           StringRedisTemplate redisTemplate,
                           ObjectMapper objectMapper,
                           KisRateLimiter kisRateLimiter) {
        this.kisWebClient = kisWebClient;
        this.kisApiProperties = kisApiProperties;
        this.kisAuthService = kisAuthService;
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
        this.kisRateLimiter = kisRateLimiter;
    }

    public KisPriceResponse getCurrentPrice(String stockCode) {
        String cacheKey = CACHE_KEY_PREFIX + stockCode;
        String cached = redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            return objectMapper.readValue(cached, KisPriceResponse.class);
        }

        try {
            KisPriceResponse response = fetchFromKis(stockCode);
            String json = objectMapper.writeValueAsString(response);
            redisTemplate.opsForValue().set(cacheKey, json, CACHE_TTL);
            redisTemplate.opsForValue().set(LATEST_KEY_PREFIX + stockCode, json);
            return response;
        } catch (RuntimeException exception) {
            String lastKnown = redisTemplate.opsForValue().get(LATEST_KEY_PREFIX + stockCode);
            if (lastKnown == null) {
                throw exception;
            }

            log.warn("KIS 현재가 조회 실패, 마지막으로 성공한 시세로 대체합니다. stockCode={}, message={}",
                    stockCode, exception.getMessage());
            return objectMapper.readValue(lastKnown, KisPriceResponse.class);
        }
    }

    private KisPriceResponse fetchFromKis(String stockCode) {
        kisRateLimiter.acquire();
        String accessToken = kisAuthService.getAccessToken();

        KisPriceEnvelope envelope = kisWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/uapi/domestic-stock/v1/quotations/inquire-price")
                        .queryParam("FID_COND_MRKT_DIV_CODE", "J")
                        .queryParam("FID_INPUT_ISCD", stockCode)
                        .build())
                .header("authorization", "Bearer " + accessToken)
                .header("appkey", kisApiProperties.appKey())
                .header("appsecret", kisApiProperties.appSecret())
                .header("tr_id", TR_ID_INQUIRE_PRICE)
                .header("custtype", "P")
                .retrieve()
                .onStatus(status -> status.value() == 500, response ->
                        response.bodyToMono(String.class).flatMap(body -> {
                            log.warn("[KIS 500 응답 본문] {}", body);
                            return response.createException();
                        }))
                .bodyToMono(KisPriceEnvelope.class)
                .block();

        if (!envelope.isSuccess()) {
            throw new IllegalStateException("KIS 시세 조회 실패: " + envelope.message());
        }

        return envelope.output();
    }

    public List<StockQuoteResponse> getMultiplePrices(List<String> stockCodes) {
        if (stockCodes.isEmpty()) {
            return List.of();
        }
        if (stockCodes.size() > MULTI_PRICE_MAX_CODES) {
            throw new IllegalArgumentException("한 번에 최대 " + MULTI_PRICE_MAX_CODES + "개 종목까지 조회할 수 있습니다.");
        }

        kisRateLimiter.acquire();
        String accessToken = kisAuthService.getAccessToken();

        KisMultiPriceEnvelope envelope = kisWebClient.get()
                .uri(uriBuilder -> {
                    uriBuilder.path("/uapi/domestic-stock/v1/quotations/intstock-multprice");
                    for (int i = 0; i < stockCodes.size(); i++) {
                        int slot = i + 1;
                        uriBuilder.queryParam("FID_COND_MRKT_DIV_CODE_" + slot, "J");
                        uriBuilder.queryParam("FID_INPUT_ISCD_" + slot, stockCodes.get(i));
                    }
                    return uriBuilder.build();
                })
                .header("authorization", "Bearer " + accessToken)
                .header("appkey", kisApiProperties.appKey())
                .header("appsecret", kisApiProperties.appSecret())
                .header("tr_id", MULTI_TR_ID)
                .header("custtype", "P")
                .retrieve()
                .bodyToMono(KisMultiPriceEnvelope.class)
                .block();

        if (!envelope.isSuccess()) {
            throw new IllegalStateException("KIS 다중 시세 조회 실패: " + envelope.message());
        }

        return Arrays.stream(envelope.output())
                .map(StockQuoteResponse::from)
                .toList();
    }

    // 여러 종목의 현재가를 한 번에 조회한다. 8초 캐시(price:quote:*)를 먼저 확인하고,
    // 캐시 미스인 종목만 모아서 다중시세 API(최대 30개씩 묶어서)로 KIS를 호출한다.
    // HoldingQueryService/HoldingPriceWarmupScheduler가 보유 종목 수만큼 개별 호출을
    // 하던 걸 대체하기 위한 용도라, 실패한 종목은 그냥 결과 맵에서 빠진다.
    public Map<String, BigDecimal> getCurrentPrices(List<String> stockCodes) {
        if (stockCodes.isEmpty()) {
            return Map.of();
        }

        List<String> distinctCodes = stockCodes.stream().distinct().toList();
        // shared 프로필(원격 Upstash)에서는 Redis 호출 한 번 한 번이 네트워크 왕복이라,
        // 종목마다 따로 GET 하면 그것만으로도 수백 ms가 든다. MGET으로 한 번에 묶는다.
        List<String> cachedValues = redisTemplate.opsForValue().multiGet(
                distinctCodes.stream().map(code -> QUOTE_CACHE_KEY_PREFIX + code).toList()
        );

        Map<String, BigDecimal> result = new LinkedHashMap<>();
        List<String> missing = new ArrayList<>();

        for (int i = 0; i < distinctCodes.size(); i++) {
            String cached = cachedValues == null ? null : cachedValues.get(i);
            if (cached != null) {
                result.put(distinctCodes.get(i), new BigDecimal(cached));
            } else {
                missing.add(distinctCodes.get(i));
            }
        }

        for (int i = 0; i < missing.size(); i += MULTI_PRICE_MAX_CODES) {
            List<String> chunk = missing.subList(i, Math.min(i + MULTI_PRICE_MAX_CODES, missing.size()));

            Map<String, BigDecimal> freshPrices = new LinkedHashMap<>();
            for (StockQuoteResponse quote : getMultiplePrices(chunk)) {
                freshPrices.put(quote.stockCode(), BigDecimal.valueOf(quote.currentPrice()));
            }

            cacheAll(freshPrices);
            result.putAll(freshPrices);
        }

        return result;
    }

    // 여러 종목 시세를 한 번의 파이프라인으로 캐싱한다 (종목마다 따로 SET 하면 그만큼
    // 네트워크 왕복이 생기므로, shared 프로필처럼 Redis가 원격일 때 특히 중요하다).
    private void cacheAll(Map<String, BigDecimal> prices) {
        if (prices.isEmpty()) {
            return;
        }

        // executePipelined가 넘겨주는 connection은 Lettuce 파이프라인용 프록시라
        // StringRedisConnection으로 캐스팅이 안 될 수 있다 (ClassCastException).
        // 캐스팅 없이 쓸 수 있는 stringCommands()로 직접 SET+EX를 건다.
        redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            prices.forEach((code, price) -> connection.stringCommands().set(
                    (QUOTE_CACHE_KEY_PREFIX + code).getBytes(StandardCharsets.UTF_8),
                    price.toPlainString().getBytes(StandardCharsets.UTF_8),
                    Expiration.seconds(QUOTE_CACHE_TTL.getSeconds()),
                    RedisStringCommands.SetOption.upsert()
            ));

            return null;
        });
    }
}