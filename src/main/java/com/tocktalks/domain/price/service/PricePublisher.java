package com.tocktalks.domain.price.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class PricePublisher {

    private static final String CHANNEL_PREFIX = "price:";
    private final StringRedisTemplate redisTemplate;

    public PricePublisher(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
    public void publish(String stockCode, String currentPrice) {
        redisTemplate.convertAndSend(CHANNEL_PREFIX + stockCode, currentPrice);
    }
}
