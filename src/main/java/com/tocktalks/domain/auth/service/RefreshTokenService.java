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

    // matches()가 false인 이유가 "발급받은 적이 없어서"인지 "이미 로테이션된 예전 토큰이라서"인지
    // 구분하는 용도. 후자는 재사용(탈취 의심) 신호로 다뤄야 한다.
    public boolean exists(Long memberId) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key(memberId)));
    }

    public void delete(Long memberId) {
        redisTemplate.delete(key(memberId));
    }

    private String key(Long memberId) {
        return KEY_PREFIX + memberId;
    }
}
