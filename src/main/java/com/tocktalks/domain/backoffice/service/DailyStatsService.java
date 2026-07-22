package com.tocktalks.domain.backoffice.service;

import com.tocktalks.domain.backoffice.dto.response.DailyStatsResponse;
import com.tocktalks.domain.backoffice.entity.DailyStats;
import com.tocktalks.domain.backoffice.repository.DailyStatsRepository;
import com.tocktalks.domain.community.repository.PostRepository;
import com.tocktalks.domain.member.repository.MemberRepository;
import com.tocktalks.domain.room.repository.RoomRepository;
import com.tocktalks.domain.trade.repository.TransactionRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
@Service
@RequiredArgsConstructor
public class DailyStatsService {

    private final DailyStatsRepository dailyStatsRepository;
    private final MemberRepository memberRepository;
    private final RoomRepository roomRepository;
    private final TransactionRepository transactionRepository;
    private final PostRepository postRepository;

    // 매일 자정 5분에 전날(막 끝난 하루) 통계를 집계해서 저장한다.
    // 자산 스냅샷 배치(00:00)와 겹치지 않도록 5분 뒤로 살짝 띄웠다.
    @Transactional
    @Scheduled(cron = "0 5 0 * * *")
    public void recordDailyStats() {
        LocalDate statDate = LocalDate.now().minusDays(1);

        if (dailyStatsRepository.existsByStatDate(statDate)) {
            return;
        }

        LocalDateTime start = statDate.atStartOfDay();
        LocalDateTime end = start.plusDays(1);

        int newMemberCount = (int) memberRepository.countByCreatedAtBetween(start, end);
        int totalMemberCount = (int) memberRepository.count();
        int newRoomCount = (int) roomRepository.countByCreatedAtBetween(start, end);
        int transactionCount = (int) transactionRepository.countByExecutedAtBetween(start, end);
        long transactionAmount = transactionRepository.sumAmountBetween(start, end).longValue();
        int newPostCount = (int) postRepository.countByCreatedAtBetween(start, end);

        dailyStatsRepository.save(DailyStats.of(
                statDate,
                newMemberCount,
                totalMemberCount,
                newRoomCount,
                transactionCount,
                transactionAmount,
                newPostCount
        ));

        log.info("일일 통계 저장 완료 (statDate={})", statDate);
    }

    public List<DailyStatsResponse> getDailyStats() {
        return dailyStatsRepository.findAllByOrderByStatDateDesc().stream()
                .map(DailyStatsResponse::from)
                .toList();
    }
}
