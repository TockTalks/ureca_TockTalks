package com.tocktalks.domain.portfolio.service;

import com.tocktalks.domain.portfolio.dto.AssetHistoryResponse;
import com.tocktalks.domain.portfolio.dto.PortfolioBalanceResponse;
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
import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
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

    // 현금 잔고만 필요한 화면(예: 종목 상세페이지의 매수 가능 금액 표시)을 위한 경량 조회.
    // 보유 종목 시세 조회(HoldingQueryService, KIS 호출 포함)를 타지 않아 DB 조회 한 번으로 끝난다.
    @Transactional(readOnly = true)
    public PortfolioBalanceResponse getBalance(Long memberId, Long roomParticipantId) {
        RoomParticipant participant = roomParticipantRepository.findById(roomParticipantId)
                .orElseThrow(() -> new IllegalArgumentException("참가자 정보를 찾을 수 없습니다."));

        if (!participant.getMemberId().equals(memberId)) {
            throw new IllegalArgumentException("해당 포트폴리오를 조회할 권한이 없습니다.");
        }

        return new PortfolioBalanceResponse(participant.getId(), participant.getBalance());
    }

    @Transactional(readOnly = true)
    public List<AssetHistoryResponse> getAssetHistory(Long memberId, Long roomParticipantId) {
        //1. 권한 검증 (내 자산 히스토리만 볼 수 있어야 함)
        RoomParticipant participant = roomParticipantRepository.findById(roomParticipantId)
            .orElseThrow(() -> new IllegalArgumentException("참가자 정보를 찾을 수 없습니다."));
        
        if (!participant.getMemberId().equals(memberId)) {
            throw new IllegalArgumentException("자산 기록을 조회할 권한이 없습니다.");
        }

        //1-2. 기본 방은 최근 4주만, 일반 방은 전체 조회
        Room room = roomRepository.findById(participant.getRoomId())
                .orElseThrow(() -> new IllegalArgumentException("방 정보를 찾을 수 없습니다."));

        List<AssetHistory> histories = room.isDefault()
                ? assetHistoryRepository.findByRoomParticipantIdAndRecordedAtAfterOrderByRecordedAtDesc(
                roomParticipantId, LocalDateTime.now().minusWeeks(4))
                : assetHistoryRepository.findAllByRoomParticipantIdOrderByRecordedAtDesc(roomParticipantId);

        
        //2. 조회 및 변환
        return histories.stream()
                .map(AssetHistoryResponse::from)
                .collect(Collectors.toList());
    }
    
    //보유 자산 변동 시 스냅샷 저장
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void recordSnapshot(RoomParticipant participant) {
        HoldingSummaryResponse summary = holdingQueryService.getHoldingSummary(
                participant.getMemberId(), participant.getId()
        );
        long stockValuation = summary.totalValuation().longValue();
        long totalAsset = participant.getBalance() + stockValuation;

        AssetHistory history = AssetHistory.create(participant.getId(), totalAsset);
        assetHistoryRepository.save(history);
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
