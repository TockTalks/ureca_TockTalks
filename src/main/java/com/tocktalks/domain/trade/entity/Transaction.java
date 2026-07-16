package com.tocktalks.domain.trade.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Entity
@Table(name = "transaction")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "room_participant_id", nullable = false)
    private Long roomParticipantId;

    @Column(name = "stock_code", nullable = false, length = 20)
    private String stockCode;

    @Column(name = "stock_name", nullable = false, length = 100)
    private String stockName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private TradeType type;

    @Column(nullable = false)
    private Long quantity;

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal price;

    @Column(name = "profit_amount", precision = 18, scale = 2)
    private BigDecimal profitAmount;

    @Column(name = "profit_rate", precision = 9, scale = 4)
    private BigDecimal profitRate;

    @Column(name = "executed_at", nullable = false)
    private LocalDateTime executedAt;

    private Transaction(
            Long roomParticipantId,
            String stockCode,
            String stockName,
            TradeType type,
            long quantity,
            BigDecimal price,
            BigDecimal profitAmount,
            BigDecimal profitRate
    ) {
        validateParticipant(roomParticipantId);
        validateStock(stockCode, stockName);
        validateQuantity(quantity);
        validatePrice(price);

        this.roomParticipantId = roomParticipantId;
        this.stockCode = stockCode;
        this.stockName = stockName;
        this.type = type;
        this.quantity = quantity;
        this.price = price.setScale(2, RoundingMode.HALF_UP);
        this.profitAmount = profitAmount;
        this.profitRate = profitRate;
        this.executedAt = LocalDateTime.now();
    }

    public static Transaction createBuy(
            Long roomParticipantId,
            String stockCode,
            String stockName,
            long quantity,
            BigDecimal price
    ) {
        return new Transaction(
                roomParticipantId,
                stockCode,
                stockName,
                TradeType.BUY,
                quantity,
                price,
                null,
                null
        );
    }

    public static Transaction createSell(
            Long roomParticipantId,
            String stockCode,
            String stockName,
            long quantity,
            BigDecimal price,
            BigDecimal avgPrice
    ) {
        validateQuantity(quantity);
        validatePrice(price);
        validateAvgPrice(avgPrice);

        BigDecimal priceDifference = price.subtract(avgPrice);

        BigDecimal profitAmount = priceDifference
                .multiply(BigDecimal.valueOf(quantity))
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal profitRate = priceDifference
                .divide(avgPrice, 6, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(4, RoundingMode.HALF_UP);

        return new Transaction(
                roomParticipantId,
                stockCode,
                stockName,
                TradeType.SELL,
                quantity,
                price,
                profitAmount,
                profitRate
        );
    }

    private static void validateParticipant(Long roomParticipantId) {
        if (roomParticipantId == null || roomParticipantId <= 0) {
            throw new IllegalArgumentException(
                    "방 참가자 ID가 올바르지 않습니다."
            );
        }
    }

    private static void validateStock(
            String stockCode,
            String stockName
    ) {
        if (stockCode == null || stockCode.isBlank()) {
            throw new IllegalArgumentException(
                    "종목 코드는 필수입니다."
            );
        }

        if (stockName == null || stockName.isBlank()) {
            throw new IllegalArgumentException(
                    "종목명은 필수입니다."
            );
        }
    }

    private static void validateQuantity(long quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException(
                    "거래 수량은 1 이상이어야 합니다."
            );
        }
    }

    private static void validatePrice(BigDecimal price) {
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "거래 가격은 0보다 커야 합니다."
            );
        }
    }

    private static void validateAvgPrice(BigDecimal avgPrice) {
        if (avgPrice == null
                || avgPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "평균 매입가는 0보다 커야 합니다."
            );
        }
    }
}