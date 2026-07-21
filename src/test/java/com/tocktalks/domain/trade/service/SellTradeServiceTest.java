package com.tocktalks.domain.trade.service;

import com.tocktalks.domain.room.entity.Room;
import com.tocktalks.domain.room.entity.RoomParticipant;
import com.tocktalks.domain.room.repository.RoomParticipantRepository;
import com.tocktalks.domain.room.repository.RoomRepository;
import com.tocktalks.domain.trade.dto.request.TradeOrderRequest;
import com.tocktalks.domain.trade.dto.response.TradeExecutionResponse;
import com.tocktalks.domain.trade.entity.TradeType;
import com.tocktalks.domain.trade.entity.Transaction;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SellTradeServiceTest {

    @Mock
    private RoomParticipantRepository
            roomParticipantRepository;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private CurrentPriceProvider
            currentPriceProvider;

    @Mock
    private SellTradeProcessor
            sellTradeProcessor;

    @InjectMocks
    private SellTradeService sellTradeService;

    @Test
    void 참가자를_잠그고_매도한_금액을_잔액에_반영한다() {
        RoomParticipant participant =
                RoomParticipant.join(
                        1L,
                        10L,
                        1_000_000L
                );

        Room room = Room.createDefault(
                1_000_000L
        );

        TradeOrderRequest request =
                new TradeOrderRequest(
                        "005930",
                        3L
                );

        Transaction transaction =
                Transaction.createSell(
                        20L,
                        "005930",
                        "삼성전자",
                        3L,
                        new BigDecimal("75000"),
                        new BigDecimal("70000")
                );

        when(roomParticipantRepository
                .findActiveForUpdate(
                        20L,
                        10L
                ))
                .thenReturn(Optional.of(participant));

        when(roomRepository.findById(1L))
                .thenReturn(Optional.of(room));

        when(currentPriceProvider
                .getCurrentPrice("005930"))
                .thenReturn(
                        new BigDecimal("75000")
                );

        when(sellTradeProcessor.process(
                20L,
                "005930",
                3L,
                new BigDecimal("75000")
        )).thenReturn(transaction);

        TradeExecutionResponse response =
                sellTradeService.sell(
                        10L,
                        20L,
                        request
                );

        assertThat(participant.getBalance())
                .isEqualTo(1_225_000L);

        assertThat(response.type())
                .isEqualTo(TradeType.SELL);
        assertThat(response.stockCode())
                .isEqualTo("005930");
        assertThat(response.stockName())
                .isEqualTo("삼성전자");
        assertThat(response.quantity())
                .isEqualTo(3L);
        assertThat(response.price())
                .isEqualByComparingTo("75000.00");
        assertThat(response.tradeAmount())
                .isEqualTo(225_000L);
        assertThat(response.balance())
                .isEqualTo(1_225_000L);
        assertThat(response.profitAmount())
                .isEqualByComparingTo("15000.00");
        assertThat(response.profitRate())
                .isEqualByComparingTo("7.1429");

        verify(roomParticipantRepository)
                .findActiveForUpdate(
                        20L,
                        10L
                );

        verify(sellTradeProcessor).process(
                20L,
                "005930",
                3L,
                new BigDecimal("75000")
        );
    }

    @Test
    void 보유_수량이_부족하면_잔액을_증가시키지_않는다() {
        RoomParticipant participant =
                RoomParticipant.join(
                        1L,
                        10L,
                        1_000_000L
                );

        Room room = Room.createDefault(
                1_000_000L
        );

        TradeOrderRequest request =
                new TradeOrderRequest(
                        "005930",
                        3L
                );

        when(roomParticipantRepository
                .findActiveForUpdate(
                        20L,
                        10L
                ))
                .thenReturn(Optional.of(participant));

        when(roomRepository.findById(1L))
                .thenReturn(Optional.of(room));

        when(currentPriceProvider
                .getCurrentPrice("005930"))
                .thenReturn(
                        new BigDecimal("75000")
                );

        when(sellTradeProcessor.process(
                20L,
                "005930",
                3L,
                new BigDecimal("75000")
        )).thenThrow(
                new IllegalArgumentException(
                        "보유 수량이 부족합니다."
                )
        );

        assertThatThrownBy(() ->
                sellTradeService.sell(
                        10L,
                        20L,
                        request
                )
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("보유 수량이 부족합니다.");

        assertThat(participant.getBalance())
                .isEqualTo(1_000_000L);
    }

    @Test
    void 본인의_활성_참가자가_아니면_매도할_수_없다() {
        TradeOrderRequest request =
                new TradeOrderRequest(
                        "005930",
                        1L
                );

        when(roomParticipantRepository
                .findActiveForUpdate(
                        20L,
                        10L
                ))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                sellTradeService.sell(
                        10L,
                        20L,
                        request
                )
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(
                        "거래 가능한 참가자 정보를 찾을 수 없습니다."
                );

        verifyNoInteractions(
                roomRepository,
                currentPriceProvider,
                sellTradeProcessor
        );
    }

    @Test
    void 종료된_방에서는_매도할_수_없다() {
        RoomParticipant participant =
                RoomParticipant.join(
                        1L,
                        10L,
                        1_000_000L
                );

        Room room = Room.createDefault(
                1_000_000L
        );
        room.close();

        TradeOrderRequest request =
                new TradeOrderRequest(
                        "005930",
                        1L
                );

        when(roomParticipantRepository
                .findActiveForUpdate(
                        20L,
                        10L
                ))
                .thenReturn(Optional.of(participant));

        when(roomRepository.findById(1L))
                .thenReturn(Optional.of(room));

        assertThatThrownBy(() ->
                sellTradeService.sell(
                        10L,
                        20L,
                        request
                )
        ).isInstanceOf(IllegalArgumentException.class);

        assertThat(participant.getBalance())
                .isEqualTo(1_000_000L);

        verifyNoInteractions(
                currentPriceProvider,
                sellTradeProcessor
        );
    }

    @Test
    void 매도_요청이_올바르지_않으면_저장소를_호출하지_않는다() {
        TradeOrderRequest request =
                new TradeOrderRequest(
                        "00A930",
                        1L
                );

        assertThatThrownBy(() ->
                sellTradeService.sell(
                        10L,
                        20L,
                        request
                )
        ).isInstanceOf(IllegalArgumentException.class);

        verifyNoInteractions(
                roomParticipantRepository,
                roomRepository,
                currentPriceProvider,
                sellTradeProcessor
        );
    }
}