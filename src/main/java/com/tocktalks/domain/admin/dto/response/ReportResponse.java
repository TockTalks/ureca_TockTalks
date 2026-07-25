package com.tocktalks.domain.admin.dto.response;
import com.tocktalks.domain.admin.entity.Report;

import java.time.LocalDate;
import java.util.Map;

public record ReportResponse(
    Long id, Long reporterId, String reporterNickname, String targetType, Long targetId,
    Long targetMemberId, String targetMemberNickname, String reason, String targetContent, String status, LocalDate createdAt
) {
    public static ReportResponse from(Report report, Map<Long, String> nicknameById){
        return new ReportResponse(
                report.getId(), report.getReporterId(),nicknameById.get(report.getReporterId()),
                report.getTargetType(), report.getTargetId(), report.getTargetMemberId(), nicknameById.get(report.getTargetMemberId()),
                report.getReason(), report.getTargetContent(), report.getStatus(), report.getCreatedAt()
        );
    }
}
