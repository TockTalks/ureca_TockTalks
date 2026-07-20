package com.tocktalks.domain.trade.service;

import com.tocktalks.domain.room.entity.Room;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TradeAvailabilityValidatorTest {

    private final LocalDateTime startAt =
            LocalDateTime.of(
                    2026,
                    7,
                    20,
                    9,
                    0
            );

    private final LocalDateTime endAt =
            LocalDateTime.of(
                    2026,
                    7,
                    20,
                    15,
                    30
            );

    @Test
    void 거래_가능_시간에는_검증을_통과한다() {
        Room room = createRoom();

        LocalDateTime tradeTime =
                LocalDateTime.of(
                        2026,
                        7,
                        20,
                        12,
                        0
                );

        assertThatCode(() ->
                TradeAvailabilityValidator.validate(
                        room,
                        tradeTime
                )
        ).doesNotThrowAnyException();
    }

    @Test
    void 시작_시각에는_거래할_수_있다() {
        Room room = createRoom();

        assertThatCode(() ->
                TradeAvailabilityValidator.validate(
                        room,
                        startAt
                )
        ).doesNotThrowAnyException();
    }

    @Test
    void 시작_시각_전에는_거래할_수_없다() {
        Room room = createRoom();

        LocalDateTime tradeTime =
                startAt.minusNanos(1);

        assertThatThrownBy(() ->
                TradeAvailabilityValidator.validate(
                        room,
                        tradeTime
                )
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(
                        "아직 거래 시작 시간이 아닙니다."
                );
    }

    @Test
    void 종료_시각부터는_거래할_수_없다() {
        Room room = createRoom();

        assertThatThrownBy(() ->
                TradeAvailabilityValidator.validate(
                        room,
                        endAt
                )
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(
                        "거래 가능 시간이 종료되었습니다."
                );
    }

    @Test
    void 종료된_방에서는_거래할_수_없다() {
        Room room = createRoom();
        room.close();

        assertThatThrownBy(() ->
                TradeAvailabilityValidator.validate(
                        room,
                        startAt.plusHours(1)
                )
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(
                        "거래 가능한 방 상태가 아닙니다."
                );
    }

    @Test
    void 방_정보는_필수이다() {
        assertThatThrownBy(() ->
                TradeAvailabilityValidator.validate(
                        null,
                        startAt
                )
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(
                        "방 정보는 필수입니다."
                );
    }

    @Test
    void 거래_시각은_필수이다() {
        Room room = createRoom();

        assertThatThrownBy(() ->
                TradeAvailabilityValidator.validate(
                        room,
                        null
                )
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(
                        "거래 시각은 필수입니다."
                );
    }

    private Room createRoom() {
        return Room.createPrivate(
                1L,
                "거래 테스트방",
                true,
                10_000_000L,
                startAt,
                endAt,
                10
        );
    }
}