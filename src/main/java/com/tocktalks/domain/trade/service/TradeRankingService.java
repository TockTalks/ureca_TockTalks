package com.tocktalks.domain.trade.service;

import com.tocktalks.domain.ranking.service.RankingService;
import com.tocktalks.domain.room.entity.RoomParticipant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClientException;

@Slf4j
@Service
@RequiredArgsConstructor
public class TradeRankingService {

    private final TradeAssetService tradeAssetService;
    private final RankingService rankingService;

    public void updateRanking(
            RoomParticipant participant
    ) {
        try {
            long totalAsset =
                    tradeAssetService.calculateTotalAsset(
                            participant
                    );

            rankingService.updateRanking(
                    participant.getRoomId(),
                    participant.getMemberId(),
                    totalAsset,
                    participant.getInitialSeedMoney()
            );
            rankingService.broadcastRanking(participant.getRoomId());
        } catch (WebClientException exception) {
            log.warn(
                    "거래 체결 후 KIS 현재가 조회 실패로 랭킹 갱신을 건너뜁니다. "
                            + "roomParticipantId={}, message={}",
                    participant.getId(),
                    exception.getMessage()
            );
        }
    }
}