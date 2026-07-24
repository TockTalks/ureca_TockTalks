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
    private static final String ACCESS_TOKEN_KEY_PREFIX = "kis:access-token:";
    private static final String APPROVAL_KEY_KEY_PREFIX = "kis:approval-key:";
    private static final DateTimeFormatter EXPIRE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    // accessToken(1시간)/approvalKey(12시간)는 거의 안 바뀌는 값인데, 매 호출마다 Redis GET을
    // 먼저 하고 있어서 KIS 호출/웹소켓 재연결이 잦을 때 Redis GET 사용량이 크게 늘었다.
    // 여러 프로세스가 같은 값을 공유해야 하므로 Redis를 없애지는 않되, 짧은 로컬(JVM) 캐시를
    // 앞에 둬서 같은 프로세스 안에서의 반복 호출은 Redis를 안 타게 한다.
    private static final long LOCAL_CACHE_MS = 60_000;

    private final WebClient kisWebClient;
    private final KisApiProperties kisApiProperties;
    private final StringRedisTemplate redisTemplate;

    private volatile String localAccessToken;
    private volatile long localAccessTokenAt;
    private volatile String localApprovalKey;
    private volatile long localApprovalKeyAt;

    public KisAuthService(
            WebClient kisWebClient, KisApiProperties kisApiProperties, StringRedisTemplate redisTemplate
    ) {
        this.kisWebClient = kisWebClient;
        this.kisApiProperties = kisApiProperties;
        this.redisTemplate = redisTemplate;
    }

    public String getAccessToken() {
        long now = System.currentTimeMillis();
        if (localAccessToken != null && now - localAccessTokenAt < LOCAL_CACHE_MS) {
            return localAccessToken;
        }

        String cached = redisTemplate.opsForValue().get(accessTokenKey());
        String token = cached != null ? cached : issueAccessToken();

        localAccessToken = token;
        localAccessTokenAt = now;
        return token;
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

        redisTemplate.opsForValue().set(accessTokenKey(), response.accessToken(), ttl);
        return response.accessToken();
    }

    public String getApprovalKey() {
        long now = System.currentTimeMillis();
        if (localApprovalKey != null && now - localApprovalKeyAt < LOCAL_CACHE_MS) {
            return localApprovalKey;
        }

        String cached = redisTemplate.opsForValue().get(approvalKeyKey());
        String approvalKey = cached != null ? cached : issueApprovalKey();

        localApprovalKey = approvalKey;
        localApprovalKeyAt = now;
        return approvalKey;
    }

    private String issueApprovalKey() {
        KisApprovalRequest request = KisApprovalRequest.of(kisApiProperties.appKey(), kisApiProperties.appSecret());
        KisApprovalResponse response = kisWebClient.post()
                .uri("/oauth2/Approval")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(KisApprovalResponse.class)
                .block();

        redisTemplate.opsForValue().set(approvalKeyKey(), response.approvalKey(), Duration.ofHours(12));
        return response.approvalKey();
    }

    private String accessTokenKey() {
        return ACCESS_TOKEN_KEY_PREFIX + kisApiProperties.appKey();
    }

    private String approvalKeyKey() {
        return APPROVAL_KEY_KEY_PREFIX + kisApiProperties.appKey();
    }
}