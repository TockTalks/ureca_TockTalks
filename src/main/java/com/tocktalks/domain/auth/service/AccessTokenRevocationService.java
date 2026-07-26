package com.tocktalks.domain.auth.service;

import com.tocktalks.global.config.JwtProperties;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * 회원탈퇴 직후에도 남아 있는 액세스 토큰을 만료 시점까지 차단한다.
 */
@Slf4j
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

    // 이 체크는 모든 인증 요청마다 거치므로, Redis 장애로 여기서 예외가 나면 로그인한 사람 전원의
    // 요청이 전부 막혀버린다. Redis를 못 쓰는 상황에서는 "폐기 안 됨"으로 간주하고 통과시킨다
    // (탈퇴 직후 잠깐 기존 토큰이 안 걸릴 수 있다는 트레이드오프는 감수한다).
    public boolean isRevoked(Long memberId) {
        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(key(memberId)));
        } catch (Exception e) {
            log.warn("토큰 폐기 여부 확인 실패, Redis 장애로 간주하고 통과시킵니다. memberId={}", memberId, e);
            return false;
        }
    }

    private String key(Long memberId) {
        return KEY_PREFIX + memberId;
    }
}
