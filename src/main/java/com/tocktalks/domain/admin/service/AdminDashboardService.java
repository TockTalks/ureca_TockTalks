package com.tocktalks.domain.admin.service;

import com.tocktalks.domain.admin.dto.response.DailyMemberTradeTrend;
import com.tocktalks.domain.admin.dto.response.DashboardMembersTradesResponse;
import com.tocktalks.domain.admin.dto.response.DashboardSummaryResponse;
import com.tocktalks.domain.admin.dto.response.PopularStockResponse;
import com.tocktalks.domain.backoffice.dto.response.DailyStatsResponse;
import com.tocktalks.domain.backoffice.service.DailyStatsService;
import com.tocktalks.domain.member.repository.MemberRepository;
import com.tocktalks.domain.room.repository.RoomRepository;
import com.tocktalks.domain.trade.repository.TransactionRepository;
import com.tocktalks.global.activity.ActiveMemberTracker;
import com.tocktalks.global.activity.OnlineUserTracker;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminDashboardService {

    private static final int TREND_DAYS = 30;

    private final MemberRepository memberRepository;
    private final RoomRepository roomRepository;
    private final ActiveMemberTracker activeMemberTracker;
    private final OnlineUserTracker onlineUserTracker;
    private final TransactionRepository transactionRepository;
    private final DailyStatsService dailyStatsService;

    public DashboardSummaryResponse getSummary() {
        return new DashboardSummaryResponse(
                memberRepository.count(),
                activeMemberTracker.countToday(),
                activeMemberTracker.countLast7Days(),
                roomRepository.count(),
                roomRepository.countByStatus("recruiting"),
                roomRepository.countByStatus("ongoing"),
                roomRepository.countByStatus("closed"),
                onlineUserTracker.getOnlineCount()
        );
    }

    public DashboardMembersTradesResponse getMembersTrades(int topN) {
        List<DailyMemberTradeTrend> dailyTrend = dailyStatsService.getDailyStats().stream()
                .limit(TREND_DAYS)
                .sorted(Comparator.comparing(DailyStatsResponse::statDate))
                .map(stats -> new DailyMemberTradeTrend(
                        stats.statDate(), stats.newMemberCount(), stats.transactionCount()))
                .toList();

        LocalDateTime end = LocalDateTime.now();
        LocalDateTime start = end.minusDays(TREND_DAYS);
        List<PopularStockResponse> popularStocks = transactionRepository
                .findTopTradedStocks(start, end, PageRequest.of(0, topN))
                .stream()
                .map(row -> new PopularStockResponse((String) row[0], (String) row[1], (Long) row[2]))
                .toList();

        return new DashboardMembersTradesResponse(dailyTrend, popularStocks);
    }
}