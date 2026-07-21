package com.tocktalks.domain.trade.service;

import com.tocktalks.domain.ranking.service.RankingService;
import com.tocktalks.domain.room.entity.RoomParticipant;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TradeRankingServiceTest {

    @Mock
    private TradeAssetService tradeAssetService;

    @Mock
    private RankingService rankingService;

    @InjectMocks
    private TradeRankingService tradeRankingService;

    @Test
    void calculatedTotalAssetIsSentToRankingService() {
        RoomParticipant participant =
                mock(RoomParticipant.class);

        when(participant.getRoomId())
                .thenReturn(1L);

        when(participant.getMemberId())
                .thenReturn(10L);

        when(participant.getInitialSeedMoney())
                .thenReturn(1_000_000L);

        when(tradeAssetService.calculateTotalAsset(
                participant
        )).thenReturn(1_200_000L);

        tradeRankingService.updateRanking(
                participant
        );

        verify(tradeAssetService)
                .calculateTotalAsset(participant);

        verify(rankingService).updateRanking(
                1L,
                10L,
                1_200_000L,
                1_000_000L
        );
    }
}