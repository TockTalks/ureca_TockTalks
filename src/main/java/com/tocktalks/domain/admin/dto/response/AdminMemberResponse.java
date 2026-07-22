package com.tocktalks.domain.admin.dto.response;

import com.tocktalks.domain.member.entity.Member;
import java.time.LocalDateTime;

public record AdminMemberResponse(
        Long id,
        String email,
        String nickname,
        String provider,
        String role,
        String status,
        LocalDateTime createdAt
) {
    public static AdminMemberResponse from(Member member) {
        return new AdminMemberResponse(
                member.getId(),
                member.getEmail(),
                member.getNickname(),
                member.getProvider(),
                member.getRole(),
                member.getStatus(),
                member.getCreatedAt()
        );
    }
}