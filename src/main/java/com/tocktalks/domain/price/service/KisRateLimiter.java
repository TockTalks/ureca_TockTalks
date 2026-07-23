package com.tocktalks.domain.price.service;

import jakarta.annotation.PostConstruct;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

// 팀원들이 같은 KIS API 키를 나눠 쓰는 shared 프로필에서, 각자의 로컬 백엔드 프로세스가
// 서로의 호출을 모른 채 초당 제한을 넘기지 않도록 Redis(이미 shared 프로필에서 공용으로 쓰는
// 것과 동일한 인스턴스)에 "다음 호출 가능 시각"을 두고 조율한다. local 프로필에서도 그대로
// 동작하며(단일 프로세스라 사실상 기존과 동일), Redis가 잠깐 불가하면 로컬 제한으로 폴백한다.
@Log4j2
@Component
public class KisRateLimiter {

    private static final long INTERVAL_MS = 1250L; // 초당 0.8건
    private static final String KEY = "kis:rate-limit:next-allowed-at";

    // KEYS[1] = 다음 호출 가능 시각을 담은 키, ARGV[1] = 호출 간격(ms), ARGV[2] = 현재 시각(ms)
    // 지금 호출해도 되면 그 즉시를, 아니면 예약된 다음 슬롯을 기준으로 "몇 ms 기다려야 하는지"를
    // 계산하면서 동시에 다음 슬롯을 예약(SET)까지 원자적으로 처리한다.
    private static final String ACQUIRE_SCRIPT = """
            local nextAllowedAt = tonumber(redis.call('GET', KEYS[1]) or '0')
            local interval = tonumber(ARGV[1])
            local now = tonumber(ARGV[2])
            local base = now
            if nextAllowedAt > now then
                base = nextAllowedAt
            end
            redis.call('SET', KEYS[1], base + interval, 'PX', interval * 4)
            return base - now
            """;

    private static final DefaultRedisScript<Long> SCRIPT =
            new DefaultRedisScript<>(ACQUIRE_SCRIPT, Long.class);

    private final StringRedisTemplate redisTemplate;

    private long lastCallAt = 0L; // Redis 장애 시 폴백용

    public KisRateLimiter(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // Redis 커넥션 풀이 완전히 준비되기 전에 첫 acquire() 호출(EVAL)이 나가면 SET이
    // 반영되지 않는 콜드스타트 현상이 관찰돼서, 기동 시점에 미리 연결을 한 번 워밍업한다.
    @PostConstruct
    private void warmUpConnection() {
        try {
            redisTemplate.opsForValue().get(KEY);
        } catch (Exception e) {
            log.warn("KIS 호출 제한용 Redis 커넥션 워밍업 실패 (기능에는 영향 없음)", e);
        }
    }

    public void acquire() {
        try {
            acquireViaRedis();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("KIS 호출 대기 중 인터럽트 발생", e);
        } catch (Exception e) {
            log.warn("Redis 기반 KIS 호출 제한 실패, 로컬 제한으로 대체", e);
            acquireLocally();
        }
    }

    private void acquireViaRedis() throws InterruptedException {
        Long waitMs = redisTemplate.execute(
                SCRIPT,
                List.of(KEY),
                String.valueOf(INTERVAL_MS),
                String.valueOf(System.currentTimeMillis())
        );

        if (waitMs != null && waitMs > 0) {
            Thread.sleep(waitMs);
        }
    }

    private synchronized void acquireLocally() {
        long now = System.currentTimeMillis();
        long gap = now - lastCallAt;

        if (gap < INTERVAL_MS) {
            try {
                Thread.sleep(INTERVAL_MS - gap);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException("KIS 호출 대기 중 인터럽트 발생", e);
            }
        }

        lastCallAt = System.currentTimeMillis();
    }
}
