package com.tocktalks.domain.trade.dto.response;

import com.tocktalks.domain.trade.entity.TradeType;
import com.tocktalks.domain.trade.entity.Transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TradeHistoryResponse(
        Long transactionId,
        String stockCode,
        String stockName,
        TradeType type,
        Long quantity,
        BigDecimal price,
        BigDecimal profitAmount,
        BigDecimal profitRate,
        LocalDateTime executedAt
) {

    public static TradeHistoryResponse from(
            Transaction transaction
    ) {
        return new TradeHistoryResponse(
                transaction.getId(),
                transaction.getStockCode(),
                transaction.getStockName(),
                transaction.getType(),
                transaction.getQuantity(),
                transaction.getPrice(),
                transaction.getProfitAmount(),
                transaction.getProfitRate(),
                transaction.getExecutedAt()
        );
    }
}