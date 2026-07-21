package com.tocktalks.domain.auth.dto;

import jakarta.validation.constraints.Size;

public record MemberUpdateRequest(
        @Size(min = 2, max = 20) String nickname,
        String currentPassword,
        @Size(min = 8, max = 100) String newPassword
) {
}
