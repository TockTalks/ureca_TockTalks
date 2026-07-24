package com.tocktalks.domain.admin.dto.response;

import java.util.List;

public record DashboardRoomsRanksResponse(
        long totalRoomCount,
        long recruitingRoomCount,
        long ongoingRoomCount,
        long closedRoomCount,
        long totalParticipantCount,
        long activeParticipantCount,
        List<ReturnRateDistributionBucket> returnRateDistribution,
        List<TopUserResponse> topUsers
) {
}