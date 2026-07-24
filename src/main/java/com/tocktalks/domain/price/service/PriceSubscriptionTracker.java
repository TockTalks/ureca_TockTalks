package com.tocktalks.domain.price.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
public class PriceSubscriptionTracker {

    private static final Pattern PRICE_TOPIC = Pattern.compile("^/topic/price/(\\w+)$");

    private final KisWebSocketClient kisWebSocketClient;

    private final Map<String, Map<String, String>> sessionSubscriptions = new ConcurrentHashMap<>();
    private final Map<String, AtomicInteger> viewerCounts = new ConcurrentHashMap<>();

    public PriceSubscriptionTracker(KisWebSocketClient kisWebSocketClient) {
        this.kisWebSocketClient = kisWebSocketClient;
    }

    @EventListener
    public void handleSubscribe(SessionSubscribeEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String destination = accessor.getDestination();
        if (destination == null) return;

        Matcher matcher = PRICE_TOPIC.matcher(destination);
        if (!matcher.matches()) return;

        String stockCode = matcher.group(1);
        String sessionId = accessor.getSessionId();
        String subscriptionId = accessor.getSubscriptionId();
        if (sessionId == null || subscriptionId == null) return;

        sessionSubscriptions
                .computeIfAbsent(sessionId, key -> new ConcurrentHashMap<>())
                .put(subscriptionId, stockCode);

        increment(stockCode);
    }

    @EventListener
    public void handleUnsubscribe(SessionUnsubscribeEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = accessor.getSessionId();
        String subscriptionId = accessor.getSubscriptionId();
        if (sessionId == null || subscriptionId == null) return;

        Map<String, String> subscriptions = sessionSubscriptions.get(sessionId);
        if (subscriptions == null) return;

        String stockCode = subscriptions.remove(subscriptionId);
        if (stockCode != null) {
            decrement(stockCode);
        }
    }

    @EventListener
    public void handleDisconnect(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = accessor.getSessionId();
        if (sessionId == null) return;

        Map<String, String> subscriptions = sessionSubscriptions.remove(sessionId);
        if (subscriptions == null) return;

        subscriptions.values().forEach(this::decrement);
    }

    private void increment(String stockCode) {
        viewerCounts.computeIfAbsent(stockCode, key -> new AtomicInteger()).incrementAndGet();
    }

    private void decrement(String stockCode) {
        AtomicInteger count = viewerCounts.get(stockCode);
        if (count == null) return;

        if (count.decrementAndGet() <= 0) {
            viewerCounts.remove(stockCode);
            try {
                kisWebSocketClient.unsubscribe(stockCode);
            } catch (IOException e) {
                log.warn("[구독해제 실패] stockCode={}", stockCode, e);
            }
        }
    }
}