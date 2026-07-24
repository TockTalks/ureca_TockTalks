package com.tocktalks.domain.portfolio.dto;

import com.tocktalks.domain.trade.dto.response.HoldingResponse;

import java.math.BigDecimal;

public record PortfolioHoldingResponse(
        String stockCode, String stockName, Long quantity,
        BigDecimal avgPurchasePrice,
        BigDecimal currentPrice,
        BigDecimal evaluationAmount,
        BigDecimal profitAmount,
        BigDecimal profitRate
) {
    public static PortfolioHoldingResponse from(HoldingResponse holdingResponse) {
        return new PortfolioHoldingResponse(
                holdingResponse.stockCode(),
                holdingResponse.stockName(),
                holdingResponse.quantity(),
                holdingResponse.avgPrice(),
                holdingResponse.currentPrice(),
                holdingResponse.valuationAmount(),
                holdingResponse.profitLoss(),
                holdingResponse.profitRate()
        );
    }
}
