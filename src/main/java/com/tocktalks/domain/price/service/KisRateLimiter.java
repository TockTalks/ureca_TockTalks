package com.tocktalks.domain.price.service;

import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

@Component
public class KisRateLimiter {

    private static final int PERMITS_PER_SECOND = 2;

    private final Semaphore permits = new Semaphore(PERMITS_PER_SECOND);
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public KisRateLimiter() {
        scheduler.scheduleAtFixedRate(this::refill, 1, 1, TimeUnit.SECONDS);
    }

    private void refill() {
        permits.drainPermits();
        permits.release(PERMITS_PER_SECOND);
    }

    public void acquire() {
        try {
            permits.acquire();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("KIS 호출 대기 중 인터럽트 발생", e);
        }
    }
}