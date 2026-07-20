package com.tocktalks.domain.auth.service;

import com.tocktalks.global.config.JwtProperties;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private static final String KEY_PREFIX = "refresh-token:";

    private final RedisTemplate<String, String> redisTemplate;
    private final JwtProperties jwtProperties;

    public void save(Long memberId, String refreshToken) {
        redisTemplate.opsForValue().set(
                key(memberId), refreshToken, Duration.ofMillis(jwtProperties.getRefreshTokenExpireMs()));
    }

    public boolean matches(Long memberId, String refreshToken) {
        return refreshToken.equals(redisTemplate.opsForValue().get(key(memberId)));
    }

    public void delete(Long memberId) {
        redisTemplate.delete(key(memberId));
    }

    private String key(Long memberId) {
        return KEY_PREFIX + memberId;
    }
}
