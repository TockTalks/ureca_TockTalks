package com.tocktalks.domain.trade.service;

import java.math.BigDecimal;

public final class TradeAmountCalculator {

    private TradeAmountCalculator() {
    }

    public static long calculate(
            BigDecimal price,
            long quantity
    ) {
        validatePrice(price);
        validateQuantity(quantity);

        try {
            return price
                    .multiply(
                            BigDecimal.valueOf(quantity)
                    )
                    .longValueExact();
        } catch (ArithmeticException exception) {
            throw new IllegalArgumentException(
                    "거래 금액은 정수 원화 범위여야 합니다.",
                    exception
            );
        }
    }

    private static void validatePrice(BigDecimal price) {
        if (price == null
                || price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "거래 가격은 0보다 커야 합니다."
            );
        }
    }

    private static void validateQuantity(long quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException(
                    "거래 수량은 1 이상이어야 합니다."
            );
        }
    }
}