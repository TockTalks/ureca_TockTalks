package com.tocktalks.domain.ranking.service;

import com.tocktalks.domain.ranking.dto.response.RankingBroadcastEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RankingPublisher {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final String CHANNEL_PREFIX = "ranking:update:";

    public void publish(Long roomId, RankingBroadcastEvent event){
        redisTemplate.convertAndSend(CHANNEL_PREFIX + roomId, event);
    }

}

