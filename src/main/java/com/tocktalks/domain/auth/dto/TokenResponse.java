package com.tocktalks.domain.auth.dto;

public record TokenResponse(
        String accessToken,
        String refreshToken,
        Long memberId,
        String nickname,
        boolean newMember
) {
}
