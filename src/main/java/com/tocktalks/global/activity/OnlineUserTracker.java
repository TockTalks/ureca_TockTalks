package com.tocktalks.global.activity;

import java.util.concurrent.ConcurrentHashMap;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
public class OnlineUserTracker {

    private final ConcurrentHashMap.KeySetView<String, Boolean> connectedSessionIds = ConcurrentHashMap.newKeySet();

    @EventListener
    public void handleConnect(SessionConnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        connectedSessionIds.add(accessor.getSessionId());
    }

    @EventListener
    public void handleDisconnect(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        connectedSessionIds.remove(accessor.getSessionId());
    }

    public int getOnlineCount() {
        return connectedSessionIds.size();
    }
}