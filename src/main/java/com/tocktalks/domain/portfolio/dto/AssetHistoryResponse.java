package com.tocktalks.domain.portfolio.dto;

import com.tocktalks.domain.portfolio.entity.AssetHistory;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AssetHistoryResponse {
    private LocalDateTime recordedAt;
    private Long totalAssetValue;
    private Long transactionId;
    private String stockCode;
    private String stockName;
    private String tradeType;
    private Long quantity;
    private BigDecimal price;
    private BigDecimal profitAmount;
    private BigDecimal profitRate;

    // Entity를 DTO로 변환하는 정적 팩토리 메서드
    public static AssetHistoryResponse from(AssetHistory history) {
        return new AssetHistoryResponse(
                history.getRecordedAt(),
                history.getTotalAsset(),
                history.getTransactionId(),
                history.getStockCode(),
                history.getStockName(),
                history.getTradeType(),
                history.getQuantity(),
                history.getPrice(),
                history.getProfitAmount(),
                history.getProfitRate()
        );
    }
}
