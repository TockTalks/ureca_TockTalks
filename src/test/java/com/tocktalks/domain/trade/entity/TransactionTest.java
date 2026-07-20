package com.tocktalks.domain.trade.entity;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TransactionTest {

    @Test
    void 매수_거래_내역을_생성한다() {
        Transaction transaction = Transaction.createBuy(
                1L,
                "005930",
                "삼성전자",
                2L,
                new BigDecimal("10000")
        );

        assertThat(transaction.getRoomParticipantId())
                .isEqualTo(1L);

        assertThat(transaction.getStockCode())
                .isEqualTo("005930");

        assertThat(transaction.getStockName())
                .isEqualTo("삼성전자");

        assertThat(transaction.getType())
                .isEqualTo(TradeType.BUY);

        assertThat(transaction.getQuantity())
                .isEqualTo(2L);

        assertThat(transaction.getPrice())
                .isEqualByComparingTo(
                        new BigDecimal("10000.00")
                );

        assertThat(transaction.getProfitAmount()).isNull();
        assertThat(transaction.getProfitRate()).isNull();
        assertThat(transaction.getExecutedAt()).isNotNull();
    }

    @Test
    void 매도_거래_내역에_실현_수익을_계산한다() {
        Transaction transaction = Transaction.createSell(
                1L,
                "005930",
                "삼성전자",
                2L,
                new BigDecimal("13000"),
                new BigDecimal("10000")
        );

        assertThat(transaction.getType())
                .isEqualTo(TradeType.SELL);

        assertThat(transaction.getProfitAmount())
                .isEqualByComparingTo(
                        new BigDecimal("6000.00")
                );

        assertThat(transaction.getProfitRate())
                .isEqualByComparingTo(
                        new BigDecimal("30.0000")
                );
    }

    @Test
    void 매도_거래_내역에_실현_손실을_계산한다() {
        Transaction transaction = Transaction.createSell(
                1L,
                "005930",
                "삼성전자",
                2L,
                new BigDecimal("8000"),
                new BigDecimal("10000")
        );

        assertThat(transaction.getProfitAmount())
                .isEqualByComparingTo(
                        new BigDecimal("-4000.00")
                );

        assertThat(transaction.getProfitRate())
                .isEqualByComparingTo(
                        new BigDecimal("-20.0000")
                );
    }

    @Test
    void 거래_수량은_1_이상이어야_한다() {
        assertThatThrownBy(() -> Transaction.createBuy(
                1L,
                "005930",
                "삼성전자",
                0L,
                new BigDecimal("10000")
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(
                        "거래 수량은 1 이상이어야 합니다."
                );
    }

    @Test
    void 매도할_때_평균_매입가는_0보다_커야_한다() {
        assertThatThrownBy(() -> Transaction.createSell(
                1L,
                "005930",
                "삼성전자",
                1L,
                new BigDecimal("10000"),
                BigDecimal.ZERO
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(
                        "평균 매입가는 0보다 커야 합니다."
                );
    }
}