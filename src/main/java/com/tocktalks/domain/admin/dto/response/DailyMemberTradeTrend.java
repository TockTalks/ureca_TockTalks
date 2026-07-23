package com.tocktalks.domain.admin.dto.response;

import java.time.LocalDate;

public record DailyMemberTradeTrend(
        LocalDate date,
        int newMemberCount,
        int transactionCount
) {
}