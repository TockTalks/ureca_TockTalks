package com.tocktalks.domain.backoffice.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "daily_stats")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DailyStats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "stat_date", nullable = false)
    private LocalDate statDate;

    @Column(name = "new_member_count", nullable = false)
    private Integer newMemberCount;

    @Column(name = "total_member_count", nullable = false)
    private Integer totalMemberCount;

    @Column(name = "new_room_count", nullable = false)
    private Integer newRoomCount;

    @Column(name = "transaction_count", nullable = false)
    private Integer transactionCount;

    @Column(name = "transaction_amount", nullable = false)
    private Long transactionAmount;

    @Column(name = "new_post_count", nullable = false)
    private Integer newPostCount;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public static DailyStats of(
            LocalDate statDate,
            int newMemberCount,
            int totalMemberCount,
            int newRoomCount,
            int transactionCount,
            long transactionAmount,
            int newPostCount
    ) {
        DailyStats stats = new DailyStats();
        stats.statDate = statDate;
        stats.newMemberCount = newMemberCount;
        stats.totalMemberCount = totalMemberCount;
        stats.newRoomCount = newRoomCount;
        stats.transactionCount = transactionCount;
        stats.transactionAmount = transactionAmount;
        stats.newPostCount = newPostCount;
        stats.createdAt = LocalDateTime.now();
        return stats;
    }
}
