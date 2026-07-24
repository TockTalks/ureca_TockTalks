package com.tocktalks.domain.portfolio.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "asset_history")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AssetHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "room_participant_id", nullable = false)
    private Long roomParticipantId;

    @Column(name = "total_asset", nullable = false)
    private Long totalAsset;

    @Column(name = "transaction_id")
    private Long transactionId;

    @Column(name = "stock_code", length = 20)
    private String stockCode;

    @Column(name = "stock_name", length = 100)
    private String stockName;

    @Column(name = "trade_type", length = 10)
    private String tradeType; //BUY / SELL

    private Long quantity;

    @Column(precision = 18, scale = 2)
    private BigDecimal price;

    @Column(name = "profit_amount", precision = 18, scale = 2)
    private BigDecimal profitAmount;

    @Column(name = "profit_rate", precision = 9, scale = 4)
    private BigDecimal profitRate;

    @Column(name = "recorded_at", nullable = false)
    private LocalDateTime recordedAt;

    private AssetHistory(
            Long roomParticipantId,
            Long totalAsset,
            Long transactionId,
            String stockCode,
            String stockName,
            String tradeType,
            Long quantity,
            BigDecimal price,
            BigDecimal profitAmount,
            BigDecimal profitRate,
            LocalDateTime recordedAt
    ) {
        this.roomParticipantId = roomParticipantId;
        this.totalAsset = totalAsset;
        this.transactionId = transactionId;
        this.stockCode = stockCode;
        this.stockName = stockName;
        this.tradeType = tradeType;
        this.quantity = quantity;
        this.price = price;
        this.profitAmount = profitAmount;
        this.profitRate = profitRate;
        this.recordedAt = recordedAt;
    }

    //Transaction 엔티티 대신 이벤트에서 꺼낸 값을 직접 받음 (커밋 이후 시점에 조립 -> 엔티티 재조회 불필요)
    public static AssetHistory create(
            Long roomParticipantId,
            Long totalAsset,
            Long transactionId,
            String stockCode,
            String stockName,
            String tradeType,
            Long quantity,
            BigDecimal price,
            BigDecimal profitAmount,
            BigDecimal profitRate
    ) {
        return new AssetHistory(
                roomParticipantId, totalAsset, transactionId, stockCode, stockName,
                tradeType, quantity, price, profitAmount, profitRate, LocalDateTime.now()
        );
    }
}
