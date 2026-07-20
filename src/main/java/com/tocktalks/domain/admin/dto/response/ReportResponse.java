package com.tocktalks.domain.admin.dto.response;
import com.tocktalks.domain.admin.entity.Report;

import java.time.LocalDate;

public record ReportResponse(
    Long id, Long reporterId, String targetType, Long targetId,
    String reason, String status, LocalDate createdAt
) {
    public static ReportResponse from(Report report){
        return new ReportResponse(
                report.getId(), report.getReporterId(), report.getTargetType(),
                report.getTargetId(), report.getReason(), report.getStatus(), report.getCreatedAt()
        );
    }
}
