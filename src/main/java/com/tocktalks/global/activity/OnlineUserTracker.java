package com.tocktalks.global.activity;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

// 접속 세션을 Redis Sorted Set(점수=마지막으로 살아있음을 확인한 시각)으로 관리한다.
// 정상 종료(SessionDisconnectEvent)는 즉시 제거하지만, 백엔드 프로세스가 강제종료/크래시되면 그 인스턴스가 물고 있던 세션은 disconnect 이벤트 없이 영영 안 지워질 수 있다.
// 그래서 살아있는 동안 주기적으로 타임스탬프를 갱신하고, 집계 시점엔 "최근에 갱신된 것만" 세서 죽은 세션이 자동으로 빠지게 한다.
// 접속자 수 집계용이라 Redis 장애가 실제 접속/거래를 막아선 안 된다 - 전부 fail-open으로 처리한다.
@Slf4j
@Component
@RequiredArgsConstructor
public class OnlineUserTracker {

    private static final String ONLINE_SESSIONS_KEY = "online:sessions:v2";
    private static final long HEARTBEAT_INTERVAL_MS = 30_000L;
    private static final long STALE_THRESHOLD_MS = 90_000L; // 하트비트 3번 놓치면 죽은 걸로 침

    private final RedisTemplate<String, String> redisTemplate;
    private final Set<String> localSessionIds = ConcurrentHashMap.newKeySet();

    @EventListener
    public void handleConnect(SessionConnectEvent event) {
        String sessionId = StompHeaderAccessor.wrap(event.getMessage()).getSessionId();
        localSessionIds.add(sessionId);
        try {
            redisTemplate.opsForZSet().add(ONLINE_SESSIONS_KEY, sessionId, System.currentTimeMillis());
        } catch (Exception e) {
            log.warn("접속자 기록 실패, Redis 장애로 간주하고 건너뜁니다. sessionId={}, error={}", sessionId, e.getMessage());
        }
    }

    @EventListener
    public void handleDisconnect(SessionDisconnectEvent event) {
        String sessionId = StompHeaderAccessor.wrap(event.getMessage()).getSessionId();
        localSessionIds.remove(sessionId);
        try {
            redisTemplate.opsForZSet().remove(ONLINE_SESSIONS_KEY, sessionId);
        } catch (Exception e) {
            log.warn("접속자 제거 실패, Redis 장애로 간주하고 건너뜁니다. sessionId={}, error={}", sessionId, e.getMessage());
        }
    }

    // 이 인스턴스가 물고 있는 세션들의 타임스탬프를 주기적으로 갱신해서, 이 프로세스가 살아있는 한 "최근에 살아있었음" 상태를 유지시킨다.
    // 오래 갱신 안 된(=다른 인스턴스가 죽어서 방치된) 항목은 Redis에서 아예 지운다.
    @Scheduled(fixedRate = HEARTBEAT_INTERVAL_MS)
    public void refreshLocalSessions() {
        try {
            long now = System.currentTimeMillis();
            for (String sessionId : localSessionIds) {
                redisTemplate.opsForZSet().add(ONLINE_SESSIONS_KEY, sessionId, now);
            }
            redisTemplate.opsForZSet().removeRangeByScore(ONLINE_SESSIONS_KEY, 0, now - STALE_THRESHOLD_MS);
        } catch (Exception e) {
            log.warn("접속자 하트비트 갱신 실패, Redis 장애로 간주하고 다음 주기를 기다립니다. error={}", e.getMessage());
        }
    }

    public int getOnlineCount() {
        try {
            long now = System.currentTimeMillis();
            Long count = redisTemplate.opsForZSet()
                    .count(ONLINE_SESSIONS_KEY, now - STALE_THRESHOLD_MS, now + 1000);
            return count == null ? 0 : count.intValue();
        } catch (Exception e) {
            log.warn("접속자 수 조회 실패, Redis 장애로 간주하고 0을 반환합니다. error={}", e.getMessage());
            return 0;
        }
    }
}