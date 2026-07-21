package com.tocktalks.domain.member.dto.response;

import com.tocktalks.domain.member.entity.FavoriteStock;

public record FavoriteStockResponse(
        Long id,
        String stockCode,
        String stockName
) {
    public static FavoriteStockResponse from(FavoriteStock entity) {
        return new FavoriteStockResponse(entity.getId(), entity.getStockCode(), entity.getStockName());
    }
}