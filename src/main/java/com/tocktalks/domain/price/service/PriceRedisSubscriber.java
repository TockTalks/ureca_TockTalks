package com.tocktalks.domain.price.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PriceRedisSubscriber implements MessageListener {

    private static final String CHANNEL_PREFIX = "price:";
    private final SimpMessagingTemplate messagingTemplate;

    public PriceRedisSubscriber(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String channel = new String(message.getChannel());
        String stockCode = channel.substring(CHANNEL_PREFIX.length());
        String currentPrice = new String(message.getBody());

        log.debug("[Redis 구독 수신] {} = {}", channel, currentPrice);

        messagingTemplate.convertAndSend("/topic/price/" + stockCode, currentPrice);
    }
}
