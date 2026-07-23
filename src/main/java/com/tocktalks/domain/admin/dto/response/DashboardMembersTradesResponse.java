package com.tocktalks.domain.admin.dto.response;

import java.util.List;

public record DashboardMembersTradesResponse(
        List<DailyMemberTradeTrend> dailyTrend,
        List<PopularStockResponse> popularStocks
) {
}