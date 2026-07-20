package com.tocktalks.domain.ranking.service;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

@Component
public class RankingSubscriber implements MessageListener {
    @Override
    public void onMessage(Message message, byte[] pattern){

    }
}
