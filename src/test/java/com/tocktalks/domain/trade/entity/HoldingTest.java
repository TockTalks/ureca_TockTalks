package com.tocktalks.domain.trade.entity;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class HoldingTest {

    @Test
    void 최초_매수로_보유_종목을_생성한다() {
        Holding holding = Holding.create(
                1L,
                "005930",
                "삼성전자",
                2L,
                new BigDecimal("10000")
        );

        assertThat(holding.getRoomParticipantId()).isEqualTo(1L);
        assertThat(holding.getStockCode()).isEqualTo("005930");
        assertThat(holding.getQuantity()).isEqualTo(2L);
        assertThat(holding.getAvgPrice())
                .isEqualByComparingTo(new BigDecimal("10000.00"));
    }

    @Test
    void 추가_매수하면_수량과_평균_매입가가_변경된다() {
        Holding holding = Holding.create(
                1L,
                "005930",
                "삼성전자",
                2L,
                new BigDecimal("10000")
        );

        holding.buy(1L, new BigDecimal("13000"));

        assertThat(holding.getQuantity()).isEqualTo(3L);
        assertThat(holding.getAvgPrice())
                .isEqualByComparingTo(new BigDecimal("11000.00"));
    }

    @Test
    void 일부_매도하면_수량만_감소한다() {
        Holding holding = Holding.create(
                1L,
                "005930",
                "삼성전자",
                3L,
                new BigDecimal("11000")
        );

        holding.sell(1L);

        assertThat(holding.getQuantity()).isEqualTo(2L);
        assertThat(holding.getAvgPrice())
                .isEqualByComparingTo(new BigDecimal("11000.00"));
        assertThat(holding.isEmpty()).isFalse();
    }

    @Test
    void 전량_매도하면_빈_Holding이_된다() {
        Holding holding = Holding.create(
                1L,
                "005930",
                "삼성전자",
                2L,
                new BigDecimal("10000")
        );

        holding.sell(2L);

        assertThat(holding.getQuantity()).isZero();
        assertThat(holding.isEmpty()).isTrue();
    }

    @Test
    void 보유_수량보다_많이_매도할_수_없다() {
        Holding holding = Holding.create(
                1L,
                "005930",
                "삼성전자",
                2L,
                new BigDecimal("10000")
        );

        assertThatThrownBy(() -> holding.sell(3L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("보유 수량이 부족합니다.");
    }

    @Test
    void 거래_수량은_1_이상이어야_한다() {
        assertThatThrownBy(() -> Holding.create(
                1L,
                "005930",
                "삼성전자",
                0L,
                new BigDecimal("10000")
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("거래 수량은 1 이상이어야 합니다.");
    }
}