package com.tocktalks.domain.trade.dto.response;

import com.tocktalks.domain.trade.entity.Holding;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record HoldingResponse(
        Long holdingId,
        Long roomParticipantId,
        String stockCode,
        String stockName,
        Long quantity,
        BigDecimal avgPrice,
        LocalDateTime updatedAt
) {

    public static HoldingResponse from(Holding holding) {
        return new HoldingResponse(
                holding.getId(),
                holding.getRoomParticipantId(),
                holding.getStockCode(),
                holding.getStockName(),
                holding.getQuantity(),
                holding.getAvgPrice(),
                holding.getUpdatedAt()
        );
    }
}