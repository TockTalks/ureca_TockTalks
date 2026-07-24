package com.tocktalks.domain.ranking.service;

import com.tocktalks.domain.ranking.dto.response.RankingBroadcastEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class RankingPublisher {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private static final String CHANNEL_PREFIX = "ranking:update:";

    public void publish(Long roomId, RankingBroadcastEvent event){
        try{
            String json = objectMapper.writeValueAsString(event);
            redisTemplate.convertAndSend(CHANNEL_PREFIX + roomId, json);
        } catch(Exception e){
            log.warn("랭킹 브로드캐스트 발행 실패 roomId = {}", roomId, e);
        }
    }

}

