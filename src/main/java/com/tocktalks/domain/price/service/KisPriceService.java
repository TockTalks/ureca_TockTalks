package com.tocktalks.domain.price.service;

import com.tocktalks.domain.price.config.KisApiProperties;
import com.tocktalks.domain.price.dto.response.KisPriceEnvelope;
import com.tocktalks.domain.price.dto.response.KisPriceResponse;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;

@Service
public class KisPriceService {
    private static final String TR_ID_INQUIRE_PRICE = "FHKST01010100";
    private static final String CACHE_KEY_PREFIX = "price:rest:";
    private static final Duration CACHE_TTL = Duration.ofSeconds(3);

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
                .bodyToMono(KisPriceEnvelope.class)
                .block();

        if (!envelope.isSuccess()) {
            throw new IllegalStateException("KIS 시세 조회 실패: " + envelope.message());
        }

        return envelope.output();
    }
}