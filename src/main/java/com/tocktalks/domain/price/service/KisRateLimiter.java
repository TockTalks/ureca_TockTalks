package com.tocktalks.domain.price.service;

import org.springframework.stereotype.Component;

@Component
public class KisRateLimiter {

    private static final long INTERVAL_MS = 1250L; // 초당 0.8건

    private long lastCallAt = 0L;

    public synchronized void acquire() {
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