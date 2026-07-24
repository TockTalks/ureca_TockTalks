package com.tocktalks.domain.trade.service;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TradeAmountCalculatorTest {

    @Test
    void 가격과_수량으로_정수_원화_거래금액을_계산한다() {
        long result = TradeAmountCalculator.calculate(
                new BigDecimal("70000.00"),
                10L
        );

        assertThat(result).isEqualTo(700_000L);
    }

    @Test
    void 거래_가격은_0보다_커야_한다() {
        assertThatThrownBy(() ->
                TradeAmountCalculator.calculate(
                        BigDecimal.ZERO,
                        10L
                )
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(
                        "거래 가격은 0보다 커야 합니다."
                );
    }

    @Test
    void 거래_가격은_null일_수_없다() {
        assertThatThrownBy(() ->
                TradeAmountCalculator.calculate(
                        null,
                        10L
                )
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(
                        "거래 가격은 0보다 커야 합니다."
                );
    }

    @Test
    void 거래_수량은_1_이상이어야_한다() {
        assertThatThrownBy(() ->
                TradeAmountCalculator.calculate(
                        new BigDecimal("70000"),
                        0L
                )
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(
                        "거래 수량은 1 이상이어야 합니다."
                );
    }

    @Test
    void 소수_원화_거래금액은_허용하지_않는다() {
        assertThatThrownBy(() ->
                TradeAmountCalculator.calculate(
                        new BigDecimal("70000.50"),
                        1L
                )
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(
                        "거래 금액은 정수 원화 범위여야 합니다."
                );
    }

    @Test
    void long_범위를_초과한_거래금액은_허용하지_않는다() {
        assertThatThrownBy(() ->
                TradeAmountCalculator.calculate(
                        BigDecimal.valueOf(Long.MAX_VALUE),
                        2L
                )
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(
                        "거래 금액은 정수 원화 범위여야 합니다."
                );
    }
}