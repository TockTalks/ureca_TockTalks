package com.tocktalks.global.activity;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@RequiredArgsConstructor
public class OnlineUserTracker {

    private static final String ONLINE_SESSIONS_KEY = "online:sessions";

    private final RedisTemplate<String, String> redisTemplate;

    @EventListener
    public void handleConnect(SessionConnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        redisTemplate.opsForSet().add(ONLINE_SESSIONS_KEY, accessor.getSessionId());
    }

    @EventListener
    public void handleDisconnect(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        redisTemplate.opsForSet().remove(ONLINE_SESSIONS_KEY, accessor.getSessionId());
    }

    public int getOnlineCount() {
        Long size = redisTemplate.opsForSet().size(ONLINE_SESSIONS_KEY);
        return size == null ? 0 : size.intValue();
    }
}