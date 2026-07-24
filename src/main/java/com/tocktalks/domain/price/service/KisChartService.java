package com.tocktalks.domain.price.service;

import com.tocktalks.domain.price.config.KisApiProperties;
import com.tocktalks.domain.price.dto.response.DailyPriceResponse;
import com.tocktalks.domain.price.dto.response.KisDailyChartEnvelope;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@Service
public class KisChartService {

    private static final String TR_ID_DAILY_CHART = "FHKST03010100";
    private static final DateTimeFormatter REQUEST_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final String CACHE_KEY_PREFIX = "price:daily:";
    private static final Duration CACHE_TTL = Duration.ofMinutes(1);

    private final WebClient kisWebClient;
    private final KisApiProperties kisApiProperties;
    private final KisAuthService kisAuthService;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final KisRateLimiter kisRateLimiter;

    public KisChartService(WebClient kisWebClient,
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

    public List<DailyPriceResponse> getRecentDailyPrices(String stockCode, int days) {
        String cacheKey = CACHE_KEY_PREFIX + stockCode + ":" + days;
        String cached = redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            return Arrays.asList(objectMapper.readValue(cached, DailyPriceResponse[].class));
        }

        List<DailyPriceResponse> result = fetchFromKis(stockCode, days);
        redisTemplate.opsForValue().set(cacheKey, objectMapper.writeValueAsString(result), CACHE_TTL);
        return result;
    }

    private List<DailyPriceResponse> fetchFromKis(String stockCode, int days) {
        kisRateLimiter.acquire();
        String accessToken = kisAuthService.getAccessToken();
        LocalDate today = LocalDate.now();
        LocalDate from = today.minusDays(days * 2L + 5);

        KisDailyChartEnvelope envelope = kisWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/uapi/domestic-stock/v1/quotations/inquire-daily-itemchartprice")
                        .queryParam("FID_COND_MRKT_DIV_CODE", "J")
                        .queryParam("FID_INPUT_ISCD", stockCode)
                        .queryParam("FID_INPUT_DATE_1", from.format(REQUEST_DATE_FORMAT))
                        .queryParam("FID_INPUT_DATE_2", today.format(REQUEST_DATE_FORMAT))
                        .queryParam("FID_PERIOD_DIV_CODE", "D")
                        .queryParam("FID_ORG_ADJ_PRC", "0")
                        .build())
                .header("authorization", "Bearer " + accessToken)
                .header("appkey", kisApiProperties.appKey())
                .header("appsecret", kisApiProperties.appSecret())
                .header("tr_id", TR_ID_DAILY_CHART)
                .header("custtype", "P")
                .retrieve()
                .bodyToMono(KisDailyChartEnvelope.class)
                .block();

        if (!envelope.isSuccess()) {
            throw new IllegalStateException("KIS 기간별 시세 조회 실패: " + envelope.message());
        }

        return Arrays.stream(envelope.output2())
                .map(DailyPriceResponse::from)
                .sorted(Comparator.comparing(DailyPriceResponse::date).reversed())
                .limit(days)
                .sorted(Comparator.comparing(DailyPriceResponse::date))
                .toList();
    }
}