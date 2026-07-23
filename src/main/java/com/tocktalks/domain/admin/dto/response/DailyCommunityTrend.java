package com.tocktalks.domain.admin.dto.response;

import java.time.LocalDate;

public record DailyCommunityTrend(
        LocalDate date,
        int newPostCount,
        long newCommentCount
) {
}