package com.tocktalks.domain.admin.service;

import com.tocktalks.domain.admin.dto.response.DailyCommunityTrend;
import com.tocktalks.domain.admin.dto.response.DailyMemberTradeTrend;
import com.tocktalks.domain.admin.dto.response.DashboardCommunityResponse;
import com.tocktalks.domain.admin.dto.response.DashboardMembersTradesResponse;
import com.tocktalks.domain.admin.dto.response.DashboardRoomsRanksResponse;
import com.tocktalks.domain.admin.dto.response.DashboardSummaryResponse;
import com.tocktalks.domain.admin.dto.response.PopularPostResponse;
import com.tocktalks.domain.admin.dto.response.PopularStockResponse;
import com.tocktalks.domain.admin.dto.response.ReportStatusCount;
import com.tocktalks.domain.admin.dto.response.ReturnRateDistributionBucket;
import com.tocktalks.domain.admin.dto.response.TopUserResponse;
import com.tocktalks.domain.admin.repository.ReportRepository;
import com.tocktalks.domain.backoffice.dto.response.DailyStatsResponse;
import com.tocktalks.domain.backoffice.service.DailyStatsService;
import com.tocktalks.domain.community.repository.CommentRepository;
import com.tocktalks.domain.community.repository.PostRepository;
import com.tocktalks.domain.member.entity.Member;
import com.tocktalks.domain.member.repository.MemberRepository;
import com.tocktalks.domain.ranking.entity.RoomRankingArchive;
import com.tocktalks.domain.ranking.repository.RoomRankingArchiveRepository;
import com.tocktalks.domain.room.repository.RoomParticipantRepository;
import com.tocktalks.domain.room.repository.RoomRepository;
import com.tocktalks.domain.trade.repository.TransactionRepository;
import com.tocktalks.global.activity.ActiveMemberTracker;
import com.tocktalks.global.activity.OnlineUserTracker;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminDashboardService {

    private static final int TREND_DAYS = 30;
    private static final int POST_CONTENT_PREVIEW_LENGTH = 50;
    private static final List<BigDecimal> RETURN_RATE_BOUNDARIES =
            List.of(BigDecimal.valueOf(-20), BigDecimal.valueOf(-10), BigDecimal.ZERO,
                    BigDecimal.valueOf(10), BigDecimal.valueOf(20));
    private static final List<String> RETURN_RATE_LABELS =
            List.of("-20% 미만", "-20% ~ -10%", "-10% ~ 0%", "0% ~ 10%", "10% ~ 20%", "20% 이상");
    private static final List<String> REPORT_STATUSES = List.of("pending", "rejected", "deleted");

    private final MemberRepository memberRepository;
    private final RoomRepository roomRepository;
    private final RoomParticipantRepository roomParticipantRepository;
    private final RoomRankingArchiveRepository roomRankingArchiveRepository;
    private final ActiveMemberTracker activeMemberTracker;
    private final OnlineUserTracker onlineUserTracker;
    private final TransactionRepository transactionRepository;
    private final DailyStatsService dailyStatsService;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final ReportRepository reportRepository;

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

    public DashboardRoomsRanksResponse getRoomsRanks(int topN) {
        long totalRoomCount = roomRepository.count();
        long recruitingRoomCount = roomRepository.countByStatus("recruiting");
        long ongoingRoomCount = roomRepository.countByStatus("ongoing");
        long closedRoomCount = roomRepository.countByStatus("closed");

        long totalParticipantCount = roomParticipantRepository.count();
        long activeParticipantCount = roomParticipantRepository.countByStatus("ACTIVE");

        List<ReturnRateDistributionBucket> returnRateDistribution = buildReturnRateDistribution();
        List<TopUserResponse> topUsers = buildTopUsers(topN);

        return new DashboardRoomsRanksResponse(
                totalRoomCount,
                recruitingRoomCount,
                ongoingRoomCount,
                closedRoomCount,
                totalParticipantCount,
                activeParticipantCount,
                returnRateDistribution,
                topUsers
        );
    }

    public DashboardCommunityResponse getCommunity(int topN) {
        LocalDate today = LocalDate.now();
        LocalDate rangeStart = today.minusDays(TREND_DAYS - 1);
        LocalDateTime start = rangeStart.atStartOfDay();
        LocalDateTime end = today.plusDays(1).atStartOfDay();

        Map<LocalDate, Long> postCountByDate = postRepository.findCreatedAtBetween(start, end).stream()
                .collect(Collectors.groupingBy(LocalDateTime::toLocalDate, Collectors.counting()));
        Map<LocalDate, Long> commentCountByDate = commentRepository.findCreatedAtBetween(start, end).stream()
                .collect(Collectors.groupingBy(LocalDateTime::toLocalDate, Collectors.counting()));

        List<DailyCommunityTrend> dailyTrend = IntStream.range(0, TREND_DAYS)
                .mapToObj(rangeStart::plusDays)
                .map(date -> new DailyCommunityTrend(
                        date,
                        postCountByDate.getOrDefault(date, 0L).intValue(),
                        commentCountByDate.getOrDefault(date, 0L)))
                .toList();

        List<PopularPostResponse> popularPosts = postRepository
                .findAllByOrderByLikeCountDescCommentCountDesc(PageRequest.of(0, topN))
                .stream()
                .map(post -> new PopularPostResponse(
                        post.getId(),
                        truncate(post.getContent()),
                        post.getLikeCount(),
                        post.getCommentCount()))
                .toList();

        List<ReportStatusCount> reportStatusCounts = REPORT_STATUSES.stream()
                .map(status -> new ReportStatusCount(status, reportRepository.countByStatus(status)))
                .toList();

        return new DashboardCommunityResponse(dailyTrend, popularPosts, reportStatusCounts);
    }

    private List<ReturnRateDistributionBucket> buildReturnRateDistribution() {
        long[] counts = new long[RETURN_RATE_LABELS.size()];

        List<BigDecimal> returnRates = roomRankingArchiveRepository.findAll().stream()
                .map(RoomRankingArchive::getFinalReturnRate)
                .toList();

        for (BigDecimal rate : returnRates) {
            int bucketIndex = 0;
            while (bucketIndex < RETURN_RATE_BOUNDARIES.size()
                    && rate.compareTo(RETURN_RATE_BOUNDARIES.get(bucketIndex)) >= 0) {
                bucketIndex++;
            }
            counts[bucketIndex]++;
        }

        return IntStream.range(0, RETURN_RATE_LABELS.size())
                .mapToObj(i -> new ReturnRateDistributionBucket(RETURN_RATE_LABELS.get(i), counts[i]))
                .toList();
    }

    private List<TopUserResponse> buildTopUsers(int topN) {
        List<RoomRankingArchive> topArchives = roomRankingArchiveRepository
                .findAllByOrderByFinalReturnRateDesc(PageRequest.of(0, topN));

        List<Long> memberIds = topArchives.stream()
                .map(RoomRankingArchive::getMemberId)
                .toList();

        Map<Long, String> nicknameByMemberId = memberRepository.findAllById(memberIds).stream()
                .collect(Collectors.toMap(Member::getId, Member::getNickname));

        return topArchives.stream()
                .map(archive -> new TopUserResponse(
                        archive.getMemberId(),
                        nicknameByMemberId.getOrDefault(archive.getMemberId(), "알 수 없음"),
                        archive.getRoomId(),
                        archive.getFinalReturnRate()))
                .toList();
    }

    private static String truncate(String content) {
        return content.length() > POST_CONTENT_PREVIEW_LENGTH
                ? content.substring(0, POST_CONTENT_PREVIEW_LENGTH) + "..."
                : content;
    }
}