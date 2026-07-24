package com.tocktalks.domain.trade.dto.response;

import com.tocktalks.domain.trade.entity.Holding;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

public record HoldingResponse(
        Long holdingId,
        Long roomParticipantId,
        String stockCode,
        String stockName,
        Long quantity,
        BigDecimal avgPrice,
        BigDecimal currentPrice,
        BigDecimal valuationAmount,
        BigDecimal profitLoss,
        BigDecimal profitRate,
        LocalDateTime updatedAt
) {

    public static HoldingResponse from(
            Holding holding,
            BigDecimal currentPrice
    ) {
        validateCurrentPrice(currentPrice);

        BigDecimal normalizedCurrentPrice =
                currentPrice.setScale(
                        2,
                        RoundingMode.HALF_UP
                );

        BigDecimal valuationAmount =
                normalizedCurrentPrice
                        .multiply(
                                BigDecimal.valueOf(
                                        holding.getQuantity()
                                )
                        )
                        .setScale(
                                2,
                                RoundingMode.HALF_UP
                        );

        BigDecimal priceDifference =
                normalizedCurrentPrice.subtract(
                        holding.getAvgPrice()
                );

        BigDecimal profitLoss =
                priceDifference
                        .multiply(
                                BigDecimal.valueOf(
                                        holding.getQuantity()
                                )
                        )
                        .setScale(
                                2,
                                RoundingMode.HALF_UP
                        );

        BigDecimal profitRate =
                priceDifference
                        .divide(
                                holding.getAvgPrice(),
                                6,
                                RoundingMode.HALF_UP
                        )
                        .multiply(BigDecimal.valueOf(100))
                        .setScale(
                                4,
                                RoundingMode.HALF_UP
                        );

        return new HoldingResponse(
                holding.getId(),
                holding.getRoomParticipantId(),
                holding.getStockCode(),
                holding.getStockName(),
                holding.getQuantity(),
                holding.getAvgPrice(),
                normalizedCurrentPrice,
                valuationAmount,
                profitLoss,
                profitRate,
                holding.getUpdatedAt()
        );
    }

    private static void validateCurrentPrice(
            BigDecimal currentPrice
    ) {
        if (currentPrice == null
                || currentPrice.compareTo(
                BigDecimal.ZERO
        ) <= 0) {
            throw new IllegalArgumentException(
                    "현재가는 0보다 커야 합니다."
            );
        }
    }
}