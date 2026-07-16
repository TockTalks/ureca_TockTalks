package com.tocktalks.domain.price.service;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

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

        messagingTemplate.convertAndSend("/topic/price/" + stockCode, currentPrice);
    }
}
