package com.tocktalks.domain.room.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record CreateRoomRequest(
        @NotBlank @Size(max = 100) String name,
        boolean isPublic,
        @Positive Long seedMoney,
        @NotNull LocalDateTime startAt,
        @NotNull LocalDateTime endAt,
        @Positive Integer maxParticipants
) {
}
