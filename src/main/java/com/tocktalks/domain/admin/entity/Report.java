package com.tocktalks.domain.admin.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.*;

@Entity
@Table(name = "report")
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

    @Column(nullable = false, length = 255)
    private String reason;

    @Column(nullable = false, length = 20)
    private String status; // pending / resolved

    @Column(name = "created_at", nullable = false)
    private LocalDate createdAt;
    
    public void resolve() {
        this.status = "resolved";
    }
}
