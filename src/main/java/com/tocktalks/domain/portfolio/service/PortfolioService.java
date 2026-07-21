package com.tocktalks.domain.portfolio.service;

import com.tocktalks.domain.portfolio.dto.AssetHistoryResponse;
import com.tocktalks.domain.portfolio.entity.AssetHistory;
import com.tocktalks.domain.portfolio.repository.AssetHistoryRepository;
import com.tocktalks.domain.room.entity.RoomParticipant;
import com.tocktalks.domain.room.repository.RoomParticipantRepository;
import com.tocktalks.domain.trade.dto.response.HoldingSummaryResponse;
import com.tocktalks.domain.trade.service.HoldingQueryService;
import java.time.LocalDate;
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
    
    private final HoldingQueryService holdingQueryService;
    
    @Transactional(readOnly = true)
    public List<AssetHistoryResponse> getAssetHistory(Long memberId, Long roomParticipantId) {
        //1. 권한 검증 (내 자산 히스토리만 볼 수 있어야 함)
        RoomParticipant participant = roomParticipantRepository.findById(roomParticipantId)
            .orElseThrow(() -> new IllegalArgumentException("참가자 정보를 찾을 수 없습니다."));
        
        if (!participant.getMemberId().equals(memberId)) {
            throw new IllegalArgumentException("자산 기록을 조회할 권한이 없습니다.");
        }
        
        //2. 조회 및 변환
        return assetHistoryRepository.findAllByRoomParticipantIdOrderBySnapshotDateAsc(roomParticipantId)
            .stream()
            .map(AssetHistoryResponse::from)
            .collect(Collectors.toList());
    }
    
    //매일 자정(00:00)에 모든 유저의 자산을 계산하여 스냅샷으로 저장
    @Transactional
    @Scheduled(cron = "0 0 0 * * *")
    public void recordDailyAssets() {
        log.info("일일 자산 스냅샷 저장을 시작합니다.");
        LocalDate today = LocalDate.now();
        
        //1. 현재 모의투자에 참가 중인 모든 유저 목록 가져옴
        List<RoomParticipant> activeParticipants = roomParticipantRepository.findAll();
        
        for (RoomParticipant participant : activeParticipants) {
            Long participantId = participant.getId();
            
            //2. 오늘 이미 저장된 기록이 있다면 패스 (안전 장치)
            if (assetHistoryRepository.existsByRoomParticipantIdAndSnapshotDate(participantId, today)) {
                continue;
            }
            
            try {
                //3. '주식 총 평가 금액' 가져오기
                HoldingSummaryResponse summary = holdingQueryService.getHoldingSummary(
                    participant.getMemberId(),
                    participantId
                );
                long stockValuation = summary.totalValuation().longValue();
                
                //4. 총 자산 = 현재 보유 현금(Balance) + 주식 평가 금액
                long totalAsset = participant.getBalance() + stockValuation;
                
                //5. 히스토리 저장
                AssetHistory history = AssetHistory.create(participantId, totalAsset);
                assetHistoryRepository.save(history);
            } catch (Exception e) {
                //특정 유저의 에러 때문에 전체 배치가 멈추지 않도록 예외 처리
                log.error("참가자 ID{}의 자산 스냅샷 저장 실패", participantId, e);
            }
        }
        log.info("일일 자산 스냅샷 저장이 완료되었습니다.");
    }
}
