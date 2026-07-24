package com.tocktalks.domain.admin.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.*;

@Entity
@Table(name = "report",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_report_reporter_target",
                columnNames = {"reporter_id", "target_type", "target_id"}
                ))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "reporter_id", nullable = false)
    private Long reporterId;

    @Column(name = "target_type", nullable = false, length = 20)
    private String targetType; // POST / COMMENT / ROOM

    @Column(name = "target_id", nullable = false)
    private Long targetId;

    @Column(name = "target_member_id", nullable = false) //추후 방 신고까지 들어오면 변경해야 함
    private Long targetMemberId;

    @Column(nullable = false, length = 255)
    private String reason;

    @Column(nullable = false, length = 20)
    private String status; // pending / resolved

    @Column(name = "created_at", nullable = false)
    private LocalDate createdAt;

    @Lob
    @Column(name = "target_content")
    private String targetContent;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    public static Report create(Long reporterId, String targetType, Long targetId, Long targetMemberId, String reason, String targetContent) {
        Report report = new Report();
        report.reporterId = reporterId;
        report.targetType = targetType;
        report.targetId = targetId;
        report.targetMemberId = targetMemberId;
        report.reason = reason;
        report.targetContent = targetContent;
        report.status = "pending";
        report.createdAt = LocalDate.now();
        return report;
    }

    public void reject() {
        this.status = "rejected";
        this.resolvedAt = LocalDateTime.now();
    }

    public void delete(){
        this.status = "deleted";
        this.resolvedAt = LocalDateTime.now();
    }
}
