package com.tocktalks.domain.price.dto.response;

public record StockQuoteResponse(
        String stockCode,
        long currentPrice,
        double changeRate
) {
    public static StockQuoteResponse from(KisMultiPriceItem item) {
        return new StockQuoteResponse(
                item.stockCode(),
                Long.parseLong(item.currentPrice()),
                Double.parseDouble(item.changeRate())
        );
    }
}