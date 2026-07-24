package com.tocktalks.domain.backoffice.dto.response;

import com.tocktalks.domain.backoffice.entity.DailyStats;

import java.time.LocalDate;

public record DailyStatsResponse(
        LocalDate statDate,
        Integer newMemberCount,
        Integer totalMemberCount,
        Integer newRoomCount,
        Integer transactionCount,
        Long transactionAmount,
        Integer newPostCount
) {
    public static DailyStatsResponse from(DailyStats entity) {
        return new DailyStatsResponse(
                entity.getStatDate(),
                entity.getNewMemberCount(),
                entity.getTotalMemberCount(),
                entity.getNewRoomCount(),
                entity.getTransactionCount(),
                entity.getTransactionAmount(),
                entity.getNewPostCount()
        );
    }
}
