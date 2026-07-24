package com.tocktalks.domain.auth.service;

import com.tocktalks.global.config.JwtProperties;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * 회원탈퇴 직후에도 남아 있는 액세스 토큰을 만료 시점까지 차단한다.
 */
@Service
@RequiredArgsConstructor
public class AccessTokenRevocationService {

    private static final String KEY_PREFIX = "revoked-member:";

    private final RedisTemplate<String, String> redisTemplate;
    private final JwtProperties jwtProperties;

    public void revoke(Long memberId) {
        redisTemplate.opsForValue().set(
                key(memberId),
                "withdrawn",
                Duration.ofMillis(jwtProperties.getAccessTokenExpireMs())
        );
    }

    public boolean isRevoked(Long memberId) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key(memberId)));
    }

    private String key(Long memberId) {
        return KEY_PREFIX + memberId;
    }
}
