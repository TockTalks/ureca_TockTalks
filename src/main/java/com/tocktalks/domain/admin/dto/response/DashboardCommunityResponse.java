package com.tocktalks.domain.admin.dto.response;

import java.util.List;

public record DashboardCommunityResponse(
        List<DailyCommunityTrend> dailyTrend,
        List<PopularPostResponse> popularPosts,
        List<ReportStatusCount> reportStatusCounts
) {
}