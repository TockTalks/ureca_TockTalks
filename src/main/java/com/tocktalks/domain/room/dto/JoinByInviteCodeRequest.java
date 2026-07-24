package com.tocktalks.domain.room.dto;

import jakarta.validation.constraints.NotBlank;

public record JoinByInviteCodeRequest(
        @NotBlank String inviteCode
) {
}
