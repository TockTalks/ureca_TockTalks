package com.tocktalks.domain.portfolio.service;

import com.tocktalks.domain.portfolio.dto.AssetHistoryResponse;
import com.tocktalks.domain.portfolio.dto.PortfolioDetailResponse;
import com.tocktalks.domain.portfolio.dto.PortfolioHoldingResponse;
import com.tocktalks.domain.portfolio.dto.PortfolioSummaryResponse;
import com.tocktalks.domain.portfolio.entity.AssetHistory;
import com.tocktalks.domain.portfolio.repository.AssetHistoryRepository;
import com.tocktalks.domain.room.entity.Room;
import com.tocktalks.domain.room.entity.RoomParticipant;
import com.tocktalks.domain.room.repository.RoomParticipantRepository;
import com.tocktalks.domain.room.repository.RoomRepository;
import com.tocktalks.domain.trade.dto.response.HoldingResponse;
import com.tocktalks.domain.trade.dto.response.HoldingSummaryResponse;
import com.tocktalks.domain.trade.service.HoldingQueryService;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
@Service
@RequiredArgsConstructor
public class PortfolioService {
    
    private final AssetHistoryRepository assetHistoryRepository;
    private final RoomParticipantRepository roomParticipantRepository;
    private final RoomRepository roomRepository;
    
    private final HoldingQueryService holdingQueryService;

    //내 포트폴리오 목록 조회
    @Transactional(readOnly = true)
    public List<PortfolioSummaryResponse> getPortfolios(Long memberId) {
        List<RoomParticipant> participants = roomParticipantRepository.findByMemberId(memberId);

        return participants.stream()
                .map(this::toSummary)
                .collect(Collectors.toList());
    }

    //포트폴리오 상세 조회
    @Transactional(readOnly = true)
    public PortfolioDetailResponse getPortfolioDetail(Long memberId, long roomParticipantId) {
        //1. 권한 검증 (내 포트폴리오만 볼 수 있어야 함)
        RoomParticipant participant = roomParticipantRepository.findById(roomParticipantId)
                .orElseThrow(() -> new IllegalArgumentException("참가자 정보를 찾을 수 없습니다."));

        if (!participant.getMemberId().equals(memberId)) {
            throw new IllegalArgumentException("해당 포트폴리오를 조회할 권한이 없습니다.");
        }

        //2. 보유 종목 조회
        List<HoldingResponse> holdingResponses = holdingQueryService.getHoldings(memberId, roomParticipantId);
        HoldingSummaryResponse holdingSummary = HoldingSummaryResponse.from(holdingResponses);

        List<PortfolioHoldingResponse> holdings = holdingResponses.stream()
                .map(PortfolioHoldingResponse::from)
                .toList();

        //3. 요약 정보 조립 후 holdings와 합쳐서 반환
        PortfolioSummaryResponse summary = toSummary(participant, holdingSummary);
        return PortfolioDetailResponse.of(summary, holdings);
    }

    @Transactional(readOnly = true)
    public List<AssetHistoryResponse> getAssetHistory(Long memberId, Long roomParticipantId) {
        //1. 권한 검증 (내 자산 히스토리만 볼 수 있어야 함)
        RoomParticipant participant = roomParticipantRepository.findById(roomParticipantId)
            .orElseThrow(() -> new IllegalArgumentException("참가자 정보를 찾을 수 없습니다."));
        
        if (!participant.getMemberId().equals(memberId)) {
            throw new IllegalArgumentException("자산 기록을 조회할 권한이 없습니다.");
        }
        
        //2. 조회 및 변환
        return assetHistoryRepository.findAllByRoomParticipantIdOrderByRecordedAtAsc(roomParticipantId)
            .stream()
            .map(AssetHistoryResponse::from)
            .collect(Collectors.toList());
    }
    
    //매시 정각마다 자산 스냅샷 저장
    @Transactional
//    @Scheduled(cron = "0 0 * * * *")
    @Scheduled(cron = "0 */5 * * * *") //테스트용
    public void recordHourlyAssets() {
        log.info("시간별 자산 스냅샷 저장을 시작합니다.");
//        LocalDateTime hourStart = LocalDateTime.now().truncatedTo(ChronoUnit.HOURS);
//        LocalDateTime hourEnd = hourStart.plusHours(1);

        //테스트용
        LocalDateTime hourStart = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        LocalDateTime hourEnd = hourStart.plusMinutes(5);

        //1. 현재 모의투자에 참가 중인 모든 유저 목록 가져옴
        List<RoomParticipant> activeParticipants = roomParticipantRepository.findAll();

        for (RoomParticipant participant : activeParticipants) {
            Long participantId = participant.getId();

            //2. 이번 시간대에 이미 저장된 기록이 있다면 패스 (안전 장치)
            if (assetHistoryRepository.existsByRoomParticipantIdAndRecordedAtBetween(participantId, hourStart, hourEnd)) {
                continue;
            }

            try {
                //3. '주식 총 평가 금액' 가져오기
                HoldingSummaryResponse summary = holdingQueryService.getHoldingSummary(participant.getMemberId(), participantId);
                long stockValuation = summary.totalValuation().longValue();

                //4. 총 자산
                long totalAsset = participant.getBalance() + stockValuation;

                //5. 히스토리 저장
                AssetHistory history = AssetHistory.create(participantId, totalAsset);
                assetHistoryRepository.save(history);
            } catch (Exception e) {
                //특정 유저의 에러 때문에 전체 배치가 멈추지 않도록 예외 처리
                log.error("참가자 ID{}의 자산 스냅샷 저장 실패", participantId, e);
            }
        }
        log.info("시간별 자산 스냅샷 저장이 완료되었습니다.");
    }

    //RoomParticipant -> PortfolioSummaryResponse 변환 헬퍼 (목록, 상세 공용)
    private PortfolioSummaryResponse toSummary(RoomParticipant participant) {
        HoldingSummaryResponse holdingSummary = holdingQueryService.getHoldingSummary(
                participant.getMemberId(), participant.getId()
        );
        return toSummary(participant, holdingSummary);
    }

    private PortfolioSummaryResponse toSummary(RoomParticipant participant, HoldingSummaryResponse holdingSummary) {
        Room room = roomRepository.findById(participant.getRoomId())
                .orElseThrow(() -> new IllegalArgumentException("방 정보를 찾을 수 없습니다."));

        long stockValuation = holdingSummary.totalValuation().longValue();

        long totalAssetValue;
        try {
            totalAssetValue = Math.addExact(
                    participant.getBalance(),
                    stockValuation
            );
        } catch (ArithmeticException exception) {
            throw new IllegalArgumentException(
                    "총자산이 허용 범위를 초과합니다.",
                    exception
            );
        }

        return PortfolioSummaryResponse.of(
                participant, room, totalAssetValue, stockValuation,
                holdingSummary.holdings().size()
        );
    }
}
