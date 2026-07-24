package com.tocktalks.domain.trade.service;

import com.tocktalks.domain.room.entity.Room;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 거래 통합 테스트끼리 단 하나뿐인 기본방 제약을 공유하지 않도록 독립된 진행 방을 만든다.
 */
final class TradeTestRoomFactory {

    private TradeTestRoomFactory() {
    }

    static Room ongoing(long seedMoney) {
        LocalDateTime now = LocalDateTime.now();
        return Room.createPrivate(
                1L,
                "거래테스트-" + UUID.randomUUID(),
                true,
                seedMoney,
                now.minusDays(1),
                now.plusDays(1),
                100
        );
    }
}
