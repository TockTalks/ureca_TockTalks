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
class BuyTradeServiceTest {

    @Mock
    private RoomParticipantRepository
            roomParticipantRepository;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private CurrentPriceProvider
            currentPriceProvider;

    @Mock
    private StockNameProvider
            stockNameProvider;

    @Mock
    private BuyTradeProcessor
            buyTradeProcessor;

    @InjectMocks
    private BuyTradeService buyTradeService;

    @Test
    void 참가자를_잠그고_잔액을_차감한_뒤_매수한다() {
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
                Transaction.createBuy(
                        20L,
                        "005930",
                        "삼성전자",
                        3L,
                        new BigDecimal("75000")
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

        when(stockNameProvider
                .getStockName("005930"))
                .thenReturn("삼성전자");

        when(buyTradeProcessor.process(
                20L,
                "005930",
                "삼성전자",
                3L,
                new BigDecimal("75000")
        )).thenReturn(transaction);

        TradeExecutionResponse response =
                buyTradeService.buy(
                        10L,
                        20L,
                        request
                );

        assertThat(participant.getBalance())
                .isEqualTo(775_000L);

        assertThat(response.type())
                .isEqualTo(TradeType.BUY);
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
                .isEqualTo(775_000L);

        verify(roomParticipantRepository)
                .findActiveForUpdate(
                        20L,
                        10L
                );

        verify(buyTradeProcessor).process(
                20L,
                "005930",
                "삼성전자",
                3L,
                new BigDecimal("75000")
        );
    }

    @Test
    void 잔액이_부족하면_매수_처리를_실행하지_않는다() {
        RoomParticipant participant =
                RoomParticipant.join(
                        1L,
                        10L,
                        100_000L
                );

        Room room = Room.createDefault(
                100_000L
        );

        TradeOrderRequest request =
                new TradeOrderRequest(
                        "005930",
                        2L
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

        when(stockNameProvider
                .getStockName("005930"))
                .thenReturn("삼성전자");

        assertThatThrownBy(() ->
                buyTradeService.buy(
                        10L,
                        20L,
                        request
                )
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("거래 잔액이 부족합니다.");

        assertThat(participant.getBalance())
                .isEqualTo(100_000L);

        verifyNoInteractions(buyTradeProcessor);
    }

    @Test
    void 본인의_활성_참가자가_아니면_매수할_수_없다() {
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
                buyTradeService.buy(
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
                stockNameProvider,
                buyTradeProcessor
        );
    }

    @Test
    void 종료된_방에서는_매수할_수_없다() {
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
                buyTradeService.buy(
                        10L,
                        20L,
                        request
                )
        ).isInstanceOf(IllegalArgumentException.class);

        assertThat(participant.getBalance())
                .isEqualTo(1_000_000L);

        verifyNoInteractions(
                currentPriceProvider,
                stockNameProvider,
                buyTradeProcessor
        );
    }

    @Test
    void 매수_요청이_올바르지_않으면_저장소를_호출하지_않는다() {
        TradeOrderRequest request =
                new TradeOrderRequest(
                        "00A930",
                        1L
                );

        assertThatThrownBy(() ->
                buyTradeService.buy(
                        10L,
                        20L,
                        request
                )
        ).isInstanceOf(IllegalArgumentException.class);

        verifyNoInteractions(
                roomParticipantRepository,
                roomRepository,
                currentPriceProvider,
                stockNameProvider,
                buyTradeProcessor
        );
    }
}