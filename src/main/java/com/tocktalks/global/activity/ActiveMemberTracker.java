package com.tocktalks.global.activity;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ActiveMemberTracker {

    private static final String DAILY_KEY_PREFIX = "dau:";
    private static final long KEY_TTL_DAYS = 8; // WAU 계산에 최근 7일치가 필요해서 여유 있게 8일 보관
    private static final int WAU_WINDOW_DAYS = 7;

    private final RedisTemplate<String, String> redisTemplate;

    // 접속자 통계용 기록이라 요청 자체를 막을 이유가 없다 - Redis 장애 시 조용히 건너뛴다.
    public void markActive(Long memberId) {
        try {
            String key = dailyKey(LocalDate.now());
            redisTemplate.opsForSet().add(key, memberId.toString());
            redisTemplate.expire(key, Duration.ofDays(KEY_TTL_DAYS));
        } catch (Exception e) {
            log.warn("접속자 기록 실패, Redis 장애로 간주하고 건너뜁니다. memberId={}", memberId, e);
        }
    }

    public long countToday() {
        Long size = redisTemplate.opsForSet().size(dailyKey(LocalDate.now()));
        return size == null ? 0 : size;
    }

    public long countLast7Days() {
        List<String> keys = new ArrayList<>();
        for (int i = 0; i < WAU_WINDOW_DAYS; i++) {
            keys.add(dailyKey(LocalDate.now().minusDays(i)));
        }

        Set<String> union = redisTemplate.opsForSet().union(keys.get(0), keys.subList(1, keys.size()));
        return union == null ? 0 : union.size();
    }

    private String dailyKey(LocalDate date) {
        return DAILY_KEY_PREFIX + date;
    }
}