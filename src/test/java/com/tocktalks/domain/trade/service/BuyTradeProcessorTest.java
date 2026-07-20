package com.tocktalks.domain.trade.service;

import com.tocktalks.domain.trade.entity.Holding;
import com.tocktalks.domain.trade.entity.TradeType;
import com.tocktalks.domain.trade.entity.Transaction;
import com.tocktalks.domain.trade.repository.HoldingRepository;
import com.tocktalks.domain.trade.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BuyTradeProcessorTest {

    @Mock
    private HoldingRepository holdingRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private BuyTradeProcessor buyTradeProcessor;

    @Test
    void 기존_보유_종목을_추가_매수하고_거래_내역을_저장한다() {
        Holding existingHolding = Holding.create(
                1L,
                "005930",
                "삼성전자",
                10L,
                new BigDecimal("70000")
        );

        when(holdingRepository.findForUpdate(
                1L,
                "005930"
        )).thenReturn(Optional.of(existingHolding));

        when(transactionRepository.save(any(Transaction.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0)
                );

        Transaction result = buyTradeProcessor.process(
                1L,
                "005930",
                "삼성전자",
                5L,
                new BigDecimal("80000")
        );

        assertThat(existingHolding.getQuantity())
                .isEqualTo(15L);
        assertThat(existingHolding.getAvgPrice())
                .isEqualByComparingTo("73333.33");

        verify(holdingRepository).save(existingHolding);

        assertThat(result.getType())
                .isEqualTo(TradeType.BUY);
        assertThat(result.getQuantity())
                .isEqualTo(5L);
        assertThat(result.getPrice())
                .isEqualByComparingTo("80000.00");
        assertThat(result.getStockName())
                .isEqualTo("삼성전자");
    }

    @Test
    void 처음_매수하는_종목의_보유_정보와_거래_내역을_저장한다() {
        when(holdingRepository.findForUpdate(
                1L,
                "005930"
        )).thenReturn(Optional.empty());

        when(transactionRepository.save(any(Transaction.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0)
                );

        Transaction result = buyTradeProcessor.process(
                1L,
                "005930",
                "삼성전자",
                3L,
                new BigDecimal("75000")
        );

        ArgumentCaptor<Holding> holdingCaptor =
                ArgumentCaptor.forClass(Holding.class);

        verify(holdingRepository)
                .save(holdingCaptor.capture());

        Holding savedHolding = holdingCaptor.getValue();

        assertThat(savedHolding.getRoomParticipantId())
                .isEqualTo(1L);
        assertThat(savedHolding.getStockCode())
                .isEqualTo("005930");
        assertThat(savedHolding.getStockName())
                .isEqualTo("삼성전자");
        assertThat(savedHolding.getQuantity())
                .isEqualTo(3L);
        assertThat(savedHolding.getAvgPrice())
                .isEqualByComparingTo("75000.00");

        assertThat(result.getType())
                .isEqualTo(TradeType.BUY);
        assertThat(result.getRoomParticipantId())
                .isEqualTo(1L);
        assertThat(result.getStockCode())
                .isEqualTo("005930");
        assertThat(result.getQuantity())
                .isEqualTo(3L);
        assertThat(result.getPrice())
                .isEqualByComparingTo("75000.00");
    }
}