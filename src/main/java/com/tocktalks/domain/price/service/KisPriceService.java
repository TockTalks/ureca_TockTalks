package com.tocktalks.domain.price.service;

import com.tocktalks.domain.price.config.KisApiProperties;
import com.tocktalks.domain.price.dto.response.KisMultiPriceEnvelope;
import com.tocktalks.domain.price.dto.response.KisPriceEnvelope;
import com.tocktalks.domain.price.dto.response.KisPriceResponse;
import com.tocktalks.domain.price.dto.response.StockQuoteResponse;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class KisPriceService {
    private static final String TR_ID_INQUIRE_PRICE = "FHKST01010100";
    private static final String MULTI_TR_ID = "FHKST11300006";
    private static final int MULTI_PRICE_MAX_CODES = 30;
    private static final String CACHE_KEY_PREFIX = "price:rest:";
    private static final String QUOTE_CACHE_KEY_PREFIX = "price:quote:";
    private static final Duration CACHE_TTL = Duration.ofSeconds(8);

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

        KisPriceResponse response = fetchFromKis(stockCode);
        redisTemplate.opsForValue().set(cacheKey, objectMapper.writeValueAsString(response), CACHE_TTL);
        return response;
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
                            System.out.println("[KIS 500 응답 본문] " + body);
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

        Map<String, BigDecimal> result = new LinkedHashMap<>();
        List<String> missing = new ArrayList<>();

        for (String stockCode : stockCodes.stream().distinct().toList()) {
            String cached = redisTemplate.opsForValue().get(QUOTE_CACHE_KEY_PREFIX + stockCode);
            if (cached != null) {
                result.put(stockCode, new BigDecimal(cached));
            } else {
                missing.add(stockCode);
            }
        }

        for (int i = 0; i < missing.size(); i += MULTI_PRICE_MAX_CODES) {
            List<String> chunk = missing.subList(i, Math.min(i + MULTI_PRICE_MAX_CODES, missing.size()));

            for (StockQuoteResponse quote : getMultiplePrices(chunk)) {
                BigDecimal price = BigDecimal.valueOf(quote.currentPrice());
                redisTemplate.opsForValue().set(
                        QUOTE_CACHE_KEY_PREFIX + quote.stockCode(),
                        price.toPlainString(),
                        CACHE_TTL
                );
                result.put(quote.stockCode(), price);
            }
        }

        return result;
    }
}