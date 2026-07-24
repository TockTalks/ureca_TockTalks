package com.tocktalks.domain.room.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class RoomTest {

    @Test
    void 시작시각이_미래면_모집중_상태로_생성된다() {
        Room room = Room.createPrivate(
                1L, "테스트방", true, 10_000_000L,
                LocalDateTime.now().plusHours(1), LocalDateTime.now().plusDays(1), null
        );

        assertThat(room.getStatus()).isEqualTo("recruiting");
    }

    @Test
    void 시작시각이_이미_지났으면_바로_진행중_상태로_생성된다() {
        Room room = Room.createPrivate(
                1L, "즉시시작방", true, 10_000_000L,
                LocalDateTime.now().minusMinutes(1), LocalDateTime.now().plusDays(1), null
        );

        assertThat(room.getStatus()).isEqualTo("ongoing");
    }

    @Test
    void start를_호출하면_진행중_상태가_된다() {
        Room room = Room.createPrivate(
                1L, "테스트방", true, 10_000_000L,
                LocalDateTime.now().plusHours(1), LocalDateTime.now().plusDays(1), null
        );

        room.start();

        assertThat(room.getStatus()).isEqualTo("ongoing");
    }
}
