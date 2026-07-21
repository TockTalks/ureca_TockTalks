package com.tocktalks.domain.trade.entity;

import java.util.regex.Pattern;

public final class StockCodeValidator {

    private static final Pattern STOCK_CODE_PATTERN =
            Pattern.compile("\\d{6}");

    private StockCodeValidator() {
    }

    public static void validate(String stockCode) {
        if (stockCode == null || stockCode.isBlank()) {
            throw new IllegalArgumentException(
                    "종목 코드는 필수입니다."
            );
        }

        if (!STOCK_CODE_PATTERN
                .matcher(stockCode)
                .matches()) {
            throw new IllegalArgumentException(
                    "종목 코드는 6자리 숫자여야 합니다."
            );
        }
    }
}