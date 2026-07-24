package com.tocktalks.domain.admin.dto.response;

public record PopularStockResponse(
        String stockCode,
        String stockName,
        long tradeCount
) {
}