package com.tocktalks.domain.auth.service;

import com.tocktalks.global.config.LoginAttemptProperties;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginAttemptService {

    private static final String KEY_PREFIX = "login-fail:";

    private final RedisTemplate<String, String> redisTemplate;
    private final LoginAttemptProperties loginAttemptProperties;

    public boolean isLocked(String email) {
        String count = redisTemplate.opsForValue().get(key(email));
        return count != null && Integer.parseInt(count) >= loginAttemptProperties.getMaxAttempts();
    }

    public void recordFailure(String email) {
        String key = key(email);
        Long count = redisTemplate.opsForValue().increment(key);
        if (count != null && count == 1L) {
            redisTemplate.expire(key, Duration.ofMillis(loginAttemptProperties.getLockDurationMs()));
        }
    }

    public void reset(String email) {
        redisTemplate.delete(key(email));
    }

    private String key(String email) {
        return KEY_PREFIX + email;
    }
}
