package com.tocktalks.domain.trade.entity;

import com.tocktalks.domain.trade.dto.response.HoldingResponse;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "holding_archive")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class HoldingArchive {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "room_participant_id", nullable = false)
    private Long roomParticipantId;

    @Column(name = "stock_code", nullable = false, length = 20)
    private String stockCode;

    @Column(name = "stock_name", nullable = false, length = 100)
    private String stockName;

    @Column(nullable = false)
    private Long quantity;

    @Column(name = "avg_purchase_price", nullable = false, precision = 18, scale = 2)
    private BigDecimal avgPurchasePrice;

    @Column(name = "closing_price", nullable = false, precision = 18, scale = 2)
    private BigDecimal closingPrice;

    @Column(name = "evaluation_amount", nullable = false, precision = 18, scale = 2)
    private BigDecimal evaluationAmount;

    @Column(name = "profit_amount", nullable = false, precision = 18, scale = 2)
    private BigDecimal profitAmount;

    @Column(name = "profit_rate", nullable = false, precision = 9, scale = 4)
    private BigDecimal profitRate;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    private HoldingArchive(
            Long roomParticipantId,
            String stockCode,
            String stockName,
            Long quantity,
            BigDecimal avgPurchasePrice,
            BigDecimal closingPrice,
            BigDecimal evaluationAmount,
            BigDecimal profitAmount,
            BigDecimal profitRate,
            LocalDateTime createdAt
    ) {
        this.roomParticipantId = roomParticipantId;
        this.stockCode = stockCode;
        this.stockName = stockName;
        this.quantity = quantity;
        this.avgPurchasePrice = avgPurchasePrice;
        this.closingPrice = closingPrice;
        this.evaluationAmount = evaluationAmount;
        this.profitAmount = profitAmount;
        this.profitRate = profitRate;
        this.createdAt = createdAt;
    }

    // 방 종료 시점에 이미 계산되어 있는 HoldingResponse(실시간 값)를 그대로 얼려서 저장
    public static HoldingArchive from(HoldingResponse holdingResponse) {
        return new HoldingArchive(
                holdingResponse.roomParticipantId(),
                holdingResponse.stockCode(),
                holdingResponse.stockName(),
                holdingResponse.quantity(),
                holdingResponse.avgPrice(),
                holdingResponse.currentPrice(),
                holdingResponse.valuationAmount(),
                holdingResponse.profitLoss(),
                holdingResponse.profitRate(),
                LocalDateTime.now()
        );
    }
}