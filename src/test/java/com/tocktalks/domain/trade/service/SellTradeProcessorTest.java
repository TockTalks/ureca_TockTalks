package com.tocktalks.domain.trade.service;

import com.tocktalks.domain.trade.entity.Holding;
import com.tocktalks.domain.trade.entity.TradeType;
import com.tocktalks.domain.trade.entity.Transaction;
import com.tocktalks.domain.trade.repository.HoldingRepository;
import com.tocktalks.domain.trade.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SellTradeProcessorTest {

    @Mock
    private HoldingRepository holdingRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private SellTradeProcessor sellTradeProcessor;

    @Test
    void 보유_종목을_일부_매도하고_거래_내역을_저장한다() {
        Holding holding = Holding.create(
                1L,
                "005930",
                "삼성전자",
                10L,
                new BigDecimal("70000")
        );

        when(holdingRepository.findForUpdate(
                1L,
                "005930"
        )).thenReturn(Optional.of(holding));

        when(transactionRepository.save(any(Transaction.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0)
                );

        Transaction result = sellTradeProcessor.process(
                1L,
                "005930",
                4L,
                new BigDecimal("75000")
        );

        assertThat(holding.getQuantity())
                .isEqualTo(6L);

        verify(holdingRepository).save(holding);
        verify(holdingRepository, never()).delete(holding);

        assertThat(result.getType())
                .isEqualTo(TradeType.SELL);
        assertThat(result.getQuantity())
                .isEqualTo(4L);
        assertThat(result.getPrice())
                .isEqualByComparingTo("75000.00");
        assertThat(result.getProfitAmount())
                .isEqualByComparingTo("20000.00");
        assertThat(result.getProfitRate())
                .isEqualByComparingTo("7.1429");
    }

    @Test
    void 보유_종목을_전량_매도하면_보유_정보를_삭제한다() {
        Holding holding = Holding.create(
                1L,
                "005930",
                "삼성전자",
                3L,
                new BigDecimal("70000")
        );

        when(holdingRepository.findForUpdate(
                1L,
                "005930"
        )).thenReturn(Optional.of(holding));

        when(transactionRepository.save(any(Transaction.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0)
                );

        Transaction result = sellTradeProcessor.process(
                1L,
                "005930",
                3L,
                new BigDecimal("65000")
        );

        assertThat(holding.isEmpty()).isTrue();

        verify(holdingRepository).delete(holding);
        verify(holdingRepository, never())
                .save(any(Holding.class));

        assertThat(result.getType())
                .isEqualTo(TradeType.SELL);
        assertThat(result.getProfitAmount())
                .isEqualByComparingTo("-15000.00");
        assertThat(result.getProfitRate())
                .isEqualByComparingTo("-7.1429");
    }

    @Test
    void 보유_수량보다_많이_매도하면_변경하거나_거래를_저장하지_않는다() {
        Holding holding = Holding.create(
                1L,
                "005930",
                "삼성전자",
                2L,
                new BigDecimal("70000")
        );

        when(holdingRepository.findForUpdate(
                1L,
                "005930"
        )).thenReturn(Optional.of(holding));

        assertThatThrownBy(() ->
                sellTradeProcessor.process(
                        1L,
                        "005930",
                        3L,
                        new BigDecimal("75000")
                )
        ).isInstanceOf(IllegalArgumentException.class);

        assertThat(holding.getQuantity())
                .isEqualTo(2L);

        verify(holdingRepository, never())
                .save(any(Holding.class));
        verify(holdingRepository, never())
                .delete(any(Holding.class));
        verifyNoInteractions(transactionRepository);
    }
}