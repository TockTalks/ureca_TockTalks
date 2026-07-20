package com.tocktalks.domain.trade.service;

import com.tocktalks.domain.room.entity.RoomParticipant;
import com.tocktalks.domain.room.repository.RoomParticipantRepository;
import com.tocktalks.domain.trade.dto.response.HoldingResponse;
import com.tocktalks.domain.trade.entity.Holding;
import com.tocktalks.domain.trade.repository.HoldingRepository;
import com.tocktalks.domain.trade.dto.response.HoldingSummaryResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HoldingQueryServiceTest {

    @Mock
    private RoomParticipantRepository
            roomParticipantRepository;

    @Mock
    private HoldingRepository holdingRepository;

    @Mock
    private CurrentPriceProvider currentPriceProvider;

    @InjectMocks
    private HoldingQueryService holdingQueryService;

    @Test
    void 본인의_보유_종목을_종목_코드순으로_조회한다() {
        Long memberId = 10L;
        Long roomParticipantId = 20L;

        RoomParticipant participant =
                mock(RoomParticipant.class);

        when(participant.getMemberId())
                .thenReturn(memberId);

        Holding naver = Holding.create(
                roomParticipantId,
                "035420",
                "NAVER",
                3L,
                new BigDecimal("200000")
        );

        Holding samsung = Holding.create(
                roomParticipantId,
                "005930",
                "삼성전자",
                10L,
                new BigDecimal("70000")
        );

        when(roomParticipantRepository.findById(
                roomParticipantId
        )).thenReturn(Optional.of(participant));

        when(holdingRepository.findAllByRoomParticipantId(
                roomParticipantId
        )).thenReturn(List.of(naver, samsung));

        when(currentPriceProvider.getCurrentPrice(
                "005930"
        )).thenReturn(new BigDecimal("75000"));

        when(currentPriceProvider.getCurrentPrice(
                "035420"
        )).thenReturn(new BigDecimal("180000"));

        List<HoldingResponse> result =
                holdingQueryService.getHoldings(
                        memberId,
                        roomParticipantId
                );

        assertThat(result)
                .extracting(HoldingResponse::stockCode)
                .containsExactly(
                        "005930",
                        "035420"
                );

        HoldingResponse samsungResponse =
                result.getFirst();

        assertThat(samsungResponse.stockName())
                .isEqualTo("삼성전자");

        assertThat(samsungResponse.quantity())
                .isEqualTo(10L);

        assertThat(samsungResponse.avgPrice())
                .isEqualByComparingTo("70000.00");

        assertThat(samsungResponse.currentPrice())
                .isEqualByComparingTo("75000.00");

        assertThat(samsungResponse.valuationAmount())
                .isEqualByComparingTo("750000.00");

        assertThat(samsungResponse.profitLoss())
                .isEqualByComparingTo("50000.00");

        assertThat(samsungResponse.profitRate())
                .isEqualByComparingTo("7.1429");

        assertThat(samsungResponse.updatedAt())
                .isNotNull();

        verify(holdingRepository)
                .findAllByRoomParticipantId(
                        roomParticipantId
                );

        verify(currentPriceProvider)
                .getCurrentPrice("005930");

        verify(currentPriceProvider)
                .getCurrentPrice("035420");
    }

    @Test
    void 보유_종목의_평가금액과_평가손익_합계를_계산한다() {
        Long memberId = 10L;
        Long roomParticipantId = 20L;

        RoomParticipant participant =
                mock(RoomParticipant.class);

        when(participant.getMemberId())
                .thenReturn(memberId);

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

        when(roomParticipantRepository.findById(
                roomParticipantId
        )).thenReturn(Optional.of(participant));

        when(holdingRepository.findAllByRoomParticipantId(
                roomParticipantId
        )).thenReturn(List.of(naver, samsung));

        when(currentPriceProvider.getCurrentPrice(
                "005930"
        )).thenReturn(new BigDecimal("75000"));

        when(currentPriceProvider.getCurrentPrice(
                "035420"
        )).thenReturn(new BigDecimal("180000"));

        HoldingSummaryResponse result =
                holdingQueryService.getHoldingSummary(
                        memberId,
                        roomParticipantId
                );

        assertThat(result.totalValuation())
                .isEqualByComparingTo("1290000.00");

        assertThat(result.totalProfitLoss())
                .isEqualByComparingTo("-10000.00");

        assertThat(result.holdings())
                .hasSize(2);

        assertThat(result.holdings())
                .extracting(HoldingResponse::stockCode)
                .containsExactly(
                        "005930",
                        "035420"
                );
    }

    @Test
    void 존재하지_않는_참가자의_보유_종목은_조회할_수_없다() {
        Long memberId = 10L;
        Long roomParticipantId = 20L;

        when(roomParticipantRepository.findById(
                roomParticipantId
        )).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                holdingQueryService.getHoldings(
                        memberId,
                        roomParticipantId
                )
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(
                        "보유 종목을 조회할 수 있는 참가자 정보를 찾을 수 없습니다."
                );

        verifyNoInteractions(
                holdingRepository,
                currentPriceProvider
        );
    }

    @Test
    void 다른_회원의_보유_종목은_조회할_수_없다() {
        Long memberId = 10L;
        Long roomParticipantId = 20L;

        RoomParticipant participant =
                mock(RoomParticipant.class);

        when(participant.getMemberId())
                .thenReturn(999L);

        when(roomParticipantRepository.findById(
                roomParticipantId
        )).thenReturn(Optional.of(participant));

        assertThatThrownBy(() ->
                holdingQueryService.getHoldings(
                        memberId,
                        roomParticipantId
                )
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(
                        "보유 종목을 조회할 수 있는 참가자 정보를 찾을 수 없습니다."
                );

        verifyNoInteractions(
                holdingRepository,
                currentPriceProvider
        );
    }
}