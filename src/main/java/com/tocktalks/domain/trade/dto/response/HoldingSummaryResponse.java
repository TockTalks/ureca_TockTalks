package com.tocktalks.domain.trade.dto.response;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public record HoldingSummaryResponse(
        BigDecimal totalValuation,
        BigDecimal totalProfitLoss,
        List<HoldingResponse> holdings
) {

    public static HoldingSummaryResponse from(
            List<HoldingResponse> holdings
    ) {
        BigDecimal totalValuation = holdings.stream()
                .map(HoldingResponse::valuationAmount)
                .reduce(
                        BigDecimal.ZERO,
                        BigDecimal::add
                )
                .setScale(
                        2,
                        RoundingMode.HALF_UP
                );

        BigDecimal totalProfitLoss = holdings.stream()
                .map(HoldingResponse::profitLoss)
                .reduce(
                        BigDecimal.ZERO,
                        BigDecimal::add
                )
                .setScale(
                        2,
                        RoundingMode.HALF_UP
                );

        return new HoldingSummaryResponse(
                totalValuation,
                totalProfitLoss,
                List.copyOf(holdings)
        );
    }
}