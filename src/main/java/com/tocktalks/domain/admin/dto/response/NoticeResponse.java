package com.tocktalks.domain.admin.dto.response;

import com.tocktalks.domain.admin.entity.Notice;

import java.time.LocalDateTime;

public record NoticeResponse(
        Long id, Long adminId, String title, String content, LocalDateTime createAt
) {
    public static NoticeResponse from(Notice notice){
        return new NoticeResponse(
                notice.getId(), notice.getAdminId(), notice.getTitle(),
                notice.getContent(), notice.getCreatedAt()
        );
    }
}
