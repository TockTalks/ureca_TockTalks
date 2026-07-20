package com.tocktalks.domain.admin.dto.response;
import com.tocktalks.domain.admin.entity.Report;

import java.time.LocalDateTime;

public record ReportResponse(
        Long id, Long reporterId, String targetType, Long targetId,
        String resson, String status, LocalDateTime createdAt
) {
    public static ReportResponse from(Report report){
        return new ReportResponse(
                report.getId(), report.getReporterId(), report.getTargetType(),
                report.getTargetId(), report.getReason(), report.getStatus(), report.getCreatedAt()
        );
    }
}
