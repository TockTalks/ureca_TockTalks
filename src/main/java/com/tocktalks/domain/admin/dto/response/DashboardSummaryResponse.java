package com.tocktalks.domain.admin.dto.response;

public record DashboardSummaryResponse(
        long totalMemberCount,
        long dailyActiveUserCount,
        long weeklyActiveUserCount,
        long totalRoomCount,
        long recruitingRoomCount,
        long ongoingRoomCount,
        long closedRoomCount,
        int currentOnlineCount
) {
}