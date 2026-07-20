package com.tocktalks.domain.ranking.service;

import com.tocktalks.domain.ranking.dto.response.RankingUpdateEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class RankingSubscriber implements MessageListener {

    private final ObjectMapper objectMapper;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String channel = new String(message.getChannel());
            String body = new String(message.getBody());

            RankingUpdateEvent event = objectMapper.readValue(body, RankingUpdateEvent.class);

            log.info("[RankingSubscriber] channel={}, event={}", channel, event);

        } catch (Exception e) {
            log.error("랭킹 이벤트 처리 중 오류 발생", e);
        }
    }
}