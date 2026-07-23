package com.tocktalks.domain.ranking.service;

import com.tocktalks.domain.ranking.dto.response.RankingBroadcastEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class RankingSubscriber implements MessageListener {

    private static final String CHANNEL_PREFIX = "ranking:update:";

    private final ObjectMapper objectMapper;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String channel = new String(message.getChannel());
            String roomId = channel.substring(CHANNEL_PREFIX.length());
            String body = new String(message.getBody());

            RankingBroadcastEvent event = objectMapper.readValue(body, RankingBroadcastEvent.class);

            log.info("[RankingSubscriber] channel={}, event={}", channel, event);

            messagingTemplate.convertAndSend("/topic/room-ranking/" + roomId, event);
        } catch (Exception e) {
            log.error("랭킹 이벤트 처리 중 오류 발생", e);
        }
    }
}