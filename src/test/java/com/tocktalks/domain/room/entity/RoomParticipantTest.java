package com.tocktalks.domain.room.entity;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RoomParticipantTest {

    @Test
    void 매수_금액만큼_잔액을_차감한다() {
        RoomParticipant participant =
                RoomParticipant.join(
                        1L,
                        10L,
                        1_000_000L
                );

        participant.withdraw(300_000L);

        assertThat(participant.getBalance())
                .isEqualTo(700_000L);
    }

    @Test
    void 잔액이_부족하면_차감하지_않는다() {
        RoomParticipant participant =
                RoomParticipant.join(
                        1L,
                        10L,
                        100_000L
                );

        assertThatThrownBy(() ->
                participant.withdraw(100_001L)
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("거래 잔액이 부족합니다.");

        assertThat(participant.getBalance())
                .isEqualTo(100_000L);
    }

    @Test
    void 차감_금액이_양수가_아니면_예외가_발생한다() {
        RoomParticipant participant =
                RoomParticipant.join(
                        1L,
                        10L,
                        100_000L
                );

        assertThatThrownBy(() ->
                participant.withdraw(0L)
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("거래 금액은 1원 이상이어야 합니다.");

        assertThatThrownBy(() ->
                participant.withdraw(-1L)
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("거래 금액은 1원 이상이어야 합니다.");

        assertThat(participant.getBalance())
                .isEqualTo(100_000L);
    }

    @Test
    void 매도_금액만큼_잔액을_증가시킨다() {
        RoomParticipant participant =
                RoomParticipant.join(
                        1L,
                        10L,
                        1_000_000L
                );

        participant.deposit(300_000L);

        assertThat(participant.getBalance())
                .isEqualTo(1_300_000L);
    }

    @Test
    void 증가_금액이_양수가_아니면_예외가_발생한다() {
        RoomParticipant participant =
                RoomParticipant.join(
                        1L,
                        10L,
                        100_000L
                );

        assertThatThrownBy(() ->
                participant.deposit(0L)
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("거래 금액은 1원 이상이어야 합니다.");

        assertThatThrownBy(() ->
                participant.deposit(-1L)
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("거래 금액은 1원 이상이어야 합니다.");

        assertThat(participant.getBalance())
                .isEqualTo(100_000L);
    }

    @Test
    void 잔액이_long_범위를_초과하면_증가시키지_않는다() {
        RoomParticipant participant =
                RoomParticipant.join(
                        1L,
                        10L,
                        Long.MAX_VALUE
                );

        assertThatThrownBy(() ->
                participant.deposit(1L)
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("거래 잔액이 허용 범위를 초과합니다.");

        assertThat(participant.getBalance())
                .isEqualTo(Long.MAX_VALUE);
    }
}