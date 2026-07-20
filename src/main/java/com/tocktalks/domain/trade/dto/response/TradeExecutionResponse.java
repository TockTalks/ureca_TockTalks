package com.tocktalks.domain.trade.dto.response;

import com.tocktalks.domain.trade.entity.TradeType;
import com.tocktalks.domain.trade.entity.Transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TradeExecutionResponse(
        Long transactionId,
        Long roomParticipantId,
        String stockCode,
        String stockName,
        TradeType type,
        Long quantity,
        BigDecimal price,
        Long tradeAmount,
        Long balance,
        BigDecimal profitAmount,
        BigDecimal profitRate,
        LocalDateTime executedAt
) {

    public static TradeExecutionResponse from(
            Transaction transaction,
            long tradeAmount,
            long balance
    ) {
        return new TradeExecutionResponse(
                transaction.getId(),
                transaction.getRoomParticipantId(),
                transaction.getStockCode(),
                transaction.getStockName(),
                transaction.getType(),
                transaction.getQuantity(),
                transaction.getPrice(),
                tradeAmount,
                balance,
                transaction.getProfitAmount(),
                transaction.getProfitRate(),
                transaction.getExecutedAt()
        );
    }
}