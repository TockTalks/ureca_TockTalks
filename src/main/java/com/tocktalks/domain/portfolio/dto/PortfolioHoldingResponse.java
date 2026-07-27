package com.tocktalks.domain.portfolio.dto;

import com.tocktalks.domain.trade.dto.response.HoldingResponse;
import com.tocktalks.domain.trade.entity.HoldingArchive;

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

    //종료된 방은 고정된 스냅샷에서 반환
    public static PortfolioHoldingResponse fromArchive(HoldingArchive archive) {
        return new PortfolioHoldingResponse(
                archive.getStockCode(),
                archive.getStockName(),
                archive.getQuantity(),
                archive.getAvgPurchasePrice(),
                archive.getClosingPrice(),
                archive.getEvaluationAmount(),
                archive.getProfitAmount(),
                archive.getProfitRate()
        );
    }
}
