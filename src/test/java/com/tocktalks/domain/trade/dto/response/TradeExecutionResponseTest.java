package com.tocktalks.domain.trade.dto.response;

import com.tocktalks.domain.trade.entity.TradeType;
import com.tocktalks.domain.trade.entity.Transaction;
import com.tocktalks.domain.trade.service.TradeAmountCalculator;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class TradeExecutionResponseTest {

    @Test
    void BUY_거래를_체결_응답으로_변환한다() {
        Transaction transaction = Transaction.createBuy(
                20L,
                "005930",
                "삼성전자",
                10L,
                new BigDecimal("70000")
        );

        long tradeAmount =
                TradeAmountCalculator.calculate(
                        transaction.getPrice(),
                        transaction.getQuantity()
                );

        TradeExecutionResponse response =
                TradeExecutionResponse.from(
                        transaction,
                        tradeAmount,
                        9_300_000L
                );

        assertThat(response.roomParticipantId())
                .isEqualTo(20L);
        assertThat(response.stockCode())
                .isEqualTo("005930");
        assertThat(response.stockName())
                .isEqualTo("삼성전자");
        assertThat(response.type())
                .isEqualTo(TradeType.BUY);
        assertThat(response.quantity())
                .isEqualTo(10L);
        assertThat(response.price())
                .isEqualByComparingTo("70000.00");
        assertThat(response.tradeAmount())
                .isEqualTo(700_000L);
        assertThat(response.balance())
                .isEqualTo(9_300_000L);
        assertThat(response.profitAmount())
                .isNull();
        assertThat(response.profitRate())
                .isNull();
        assertThat(response.executedAt())
                .isNotNull();
    }

    @Test
    void SELL_거래를_체결_응답으로_변환한다() {
        Transaction transaction = Transaction.createSell(
                20L,
                "005930",
                "삼성전자",
                2L,
                new BigDecimal("75000"),
                new BigDecimal("70000")
        );

        long tradeAmount =
                TradeAmountCalculator.calculate(
                        transaction.getPrice(),
                        transaction.getQuantity()
                );

        TradeExecutionResponse response =
                TradeExecutionResponse.from(
                        transaction,
                        tradeAmount,
                        9_450_000L
                );

        assertThat(response.roomParticipantId())
                .isEqualTo(20L);
        assertThat(response.stockCode())
                .isEqualTo("005930");
        assertThat(response.stockName())
                .isEqualTo("삼성전자");
        assertThat(response.type())
                .isEqualTo(TradeType.SELL);
        assertThat(response.quantity())
                .isEqualTo(2L);
        assertThat(response.price())
                .isEqualByComparingTo("75000.00");
        assertThat(response.tradeAmount())
                .isEqualTo(150_000L);
        assertThat(response.balance())
                .isEqualTo(9_450_000L);
        assertThat(response.profitAmount())
                .isEqualByComparingTo("10000.00");
        assertThat(response.profitRate())
                .isEqualByComparingTo("7.1429");
        assertThat(response.executedAt())
                .isNotNull();
    }
}