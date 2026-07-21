package com.tocktalks.domain.trade.service;

import com.tocktalks.domain.ranking.service.RankingService;
import com.tocktalks.domain.room.entity.RoomParticipant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TradeRankingService {

    private final TradeAssetService tradeAssetService;
    private final RankingService rankingService;

    public void updateRanking(
            RoomParticipant participant
    ) {
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
    }
}