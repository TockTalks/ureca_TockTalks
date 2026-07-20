package com.tocktalks.domain.trade.service;

import com.tocktalks.domain.room.entity.Room;

import java.time.LocalDateTime;

public final class TradeAvailabilityValidator {

    private static final String ONGOING = "ongoing";

    private TradeAvailabilityValidator() {
    }

    public static void validate(
            Room room,
            LocalDateTime tradeTime
    ) {
        if (room == null) {
            throw new IllegalArgumentException(
                    "방 정보는 필수입니다."
            );
        }

        if (tradeTime == null) {
            throw new IllegalArgumentException(
                    "거래 시각은 필수입니다."
            );
        }

        if (!ONGOING.equals(room.getStatus())) {
            throw new IllegalArgumentException(
                    "거래 가능한 방 상태가 아닙니다."
            );
        }

        if (room.getStartAt() != null
                && tradeTime.isBefore(room.getStartAt())) {
            throw new IllegalArgumentException(
                    "아직 거래 시작 시간이 아닙니다."
            );
        }

        if (room.getEndAt() != null
                && !tradeTime.isBefore(room.getEndAt())) {
            throw new IllegalArgumentException(
                    "거래 가능 시간이 종료되었습니다."
            );
        }
    }
}