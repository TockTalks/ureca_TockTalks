package com.tocktalks.domain.portfolio.dto;

import java.math.BigDecimal;
import java.util.List;

public record PortfolioDetailResponse(
        Long roomParticipantId, Long roomId, String roomName, String roomStatus,
        Long balance, Long stockValuation, Long totalAssetValue, Long initialSeedMoney,
        Long profitAmount, BigDecimal profitRate, int holdingCount,
        List<PortfolioHoldingResponse> holdings
) {
    public static PortfolioDetailResponse of(
            PortfolioSummaryResponse summary,
            List<PortfolioHoldingResponse> holdings
    ) {
        return new PortfolioDetailResponse(
                summary.roomParticipantId(),
                summary.roomId(),
                summary.roomName(),
                summary.roomStatus(),
                summary.balance(),
                summary.stockValuation(),
                summary.totalAssetValue(),
                summary.initialSeedMoney(),
                summary.profitAmount(),
                summary.profitRate(),
                summary.holdingCount(),
                holdings
        );
    }
}
