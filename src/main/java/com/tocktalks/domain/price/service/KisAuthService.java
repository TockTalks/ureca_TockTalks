package com.tocktalks.domain.price.service;

import com.tocktalks.domain.price.config.KisApiProperties;
import com.tocktalks.domain.price.dto.request.KisApprovalRequest;
import com.tocktalks.domain.price.dto.request.KisTokenRequest;
import com.tocktalks.domain.price.dto.response.KisApprovalResponse;
import com.tocktalks.domain.price.dto.response.KisTokenResponse;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class KisAuthService {
    private static final String ACCESS_TOKEN_KEY = "kis:access-token";
    private static final String APPROVAL_KEY_KEY = "kis:approval-key";
    private static final DateTimeFormatter EXPIRE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final WebClient kisWebClient;
    private final KisApiProperties kisApiProperties;
    private final StringRedisTemplate redisTemplate;

    public KisAuthService(
            WebClient kisWebClient, KisApiProperties kisApiProperties, StringRedisTemplate redisTemplate
    ) {
        this.kisWebClient = kisWebClient;
        this.kisApiProperties = kisApiProperties;
        this.redisTemplate = redisTemplate;
    }

    public String getAccessToken() {
        String cached = redisTemplate.opsForValue().get(ACCESS_TOKEN_KEY);
        if (cached != null) {
            return cached;
        }
        return issueAccessToken();
    }

    private String issueAccessToken() {
        KisTokenRequest request = KisTokenRequest.of(kisApiProperties.appKey(), kisApiProperties.appSecret());
        KisTokenResponse response = kisWebClient.post()
                .uri("/oauth2/tokenP")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(KisTokenResponse.class)
                .block();

        LocalDateTime expiredAt = LocalDateTime.parse(response.accessTokenExpired(), EXPIRE_FORMAT);
        Duration ttl = Duration.between(LocalDateTime.now(), expiredAt).minusMinutes(1);

        redisTemplate.opsForValue().set(ACCESS_TOKEN_KEY, response.accessToken(), ttl);
        return response.accessToken();
    }

    public String getApprovalKey() {
        String cached = redisTemplate.opsForValue().get(APPROVAL_KEY_KEY);
        if (cached != null) {
            return cached;
        }
        return issueApprovalKey();
    }

    private String issueApprovalKey() {
        KisApprovalRequest request = KisApprovalRequest.of(kisApiProperties.appKey(), kisApiProperties.appSecret());
        KisApprovalResponse response = kisWebClient.post()
                .uri("/oauth2/Approval")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(KisApprovalResponse.class)
                .block();

        redisTemplate.opsForValue().set(APPROVAL_KEY_KEY, response.approvalKey(), Duration.ofHours(12));
        return response.approvalKey();

    }

}
