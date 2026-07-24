package com.tocktalks.domain.admin.dto.response;
import com.tocktalks.domain.admin.entity.Report;

import java.time.LocalDate;

public record ReportResponse(
    Long id, Long reporterId, String targetType, Long targetId,
    Long targetMemberId, String reason, String targetContent, String status, LocalDate createdAt
) {
    public static ReportResponse from(Report report){
        return new ReportResponse(
                report.getId(), report.getReporterId(), report.getTargetType(), report.getTargetId(),
                report.getTargetMemberId(), report.getReason(), report.getTargetContent(), report.getStatus(), report.getCreatedAt()
        );
    }
}
