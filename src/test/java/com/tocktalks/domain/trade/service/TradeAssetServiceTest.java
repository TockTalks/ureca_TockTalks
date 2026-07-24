package com.tocktalks.domain.trade.service;

import com.tocktalks.domain.room.entity.RoomParticipant;
import com.tocktalks.domain.trade.entity.Holding;
import com.tocktalks.domain.trade.repository.HoldingRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TradeAssetServiceTest {

    @Mock
    private HoldingRepository holdingRepository;

    @Mock
    private CurrentPriceProvider currentPriceProvider;

    @InjectMocks
    private TradeAssetService tradeAssetService;

    @Test
    void cashOnlyParticipantReturnsBalance() {
        Long roomParticipantId = 20L;

        RoomParticipant participant =
                mock(RoomParticipant.class);

        when(participant.getId())
                .thenReturn(roomParticipantId);

        when(participant.getBalance())
                .thenReturn(1_000_000L);

        when(holdingRepository.findAllByRoomParticipantId(
                roomParticipantId
        )).thenReturn(List.of());

        long result =
                tradeAssetService.calculateTotalAsset(
                        participant
                );

        assertThat(result).isEqualTo(1_000_000L);

        verify(holdingRepository)
                .findAllByRoomParticipantId(
                        roomParticipantId
                );

        verifyNoInteractions(currentPriceProvider);
    }

    @Test
    void balanceAndHoldingValuationsAreAdded() {
        Long roomParticipantId = 20L;

        RoomParticipant participant =
                mock(RoomParticipant.class);

        when(participant.getId())
                .thenReturn(roomParticipantId);

        when(participant.getBalance())
                .thenReturn(1_000_000L);

        Holding samsung = Holding.create(
                roomParticipantId,
                "005930",
                "삼성전자",
                10L,
                new BigDecimal("70000")
        );

        Holding naver = Holding.create(
                roomParticipantId,
                "035420",
                "NAVER",
                3L,
                new BigDecimal("200000")
        );

        when(holdingRepository.findAllByRoomParticipantId(
                roomParticipantId
        )).thenReturn(List.of(samsung, naver));

        when(currentPriceProvider.getCurrentPrice(
                "005930"
        )).thenReturn(new BigDecimal("75000"));

        when(currentPriceProvider.getCurrentPrice(
                "035420"
        )).thenReturn(new BigDecimal("180000"));

        long result =
                tradeAssetService.calculateTotalAsset(
                        participant
                );

        assertThat(result).isEqualTo(2_290_000L);
    }

    @Test
    void nullParticipantIsRejected() {
        assertThatThrownBy(() ->
                tradeAssetService.calculateTotalAsset(null)
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("방 참가자 정보가 올바르지 않습니다.");

        verifyNoInteractions(
                holdingRepository,
                currentPriceProvider
        );
    }

    @Test
    void totalAssetOverflowIsRejected() {
        Long roomParticipantId = 20L;

        RoomParticipant participant =
                mock(RoomParticipant.class);

        when(participant.getId())
                .thenReturn(roomParticipantId);

        when(participant.getBalance())
                .thenReturn(Long.MAX_VALUE);

        Holding holding = Holding.create(
                roomParticipantId,
                "005930",
                "삼성전자",
                1L,
                BigDecimal.ONE
        );

        when(holdingRepository.findAllByRoomParticipantId(
                roomParticipantId
        )).thenReturn(List.of(holding));

        when(currentPriceProvider.getCurrentPrice(
                "005930"
        )).thenReturn(BigDecimal.ONE);

        assertThatThrownBy(() ->
                tradeAssetService.calculateTotalAsset(
                        participant
                )
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("총자산이 허용 범위를 초과합니다.")
                .hasCauseInstanceOf(ArithmeticException.class);
    }
}