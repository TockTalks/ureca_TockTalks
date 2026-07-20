package com.tocktalks.domain.trade.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.math.RoundingMode;

@Entity
@Table(
        name = "holding",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_holding_participant_stock",
                        columnNames = {"room_participant_id", "stock_code"}
                )
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Holding {

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

    @Column(name = "avg_price", nullable = false, precision = 18, scale = 2)
    private BigDecimal avgPrice;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;


    private Holding(
            Long roomParticipantId,
            String stockCode,
            String stockName,
            long quantity,
            BigDecimal avgPrice
    ) {
        validateParticipant(roomParticipantId);
        validateStock(stockCode, stockName);
        validateQuantity(quantity);
        validatePrice(avgPrice);

        this.roomParticipantId = roomParticipantId;
        this.stockCode = stockCode;
        this.stockName = stockName;
        this.quantity = quantity;
        this.avgPrice = avgPrice.setScale(2, RoundingMode.HALF_UP);
        this.updatedAt = LocalDateTime.now();
    } // Holding 생성 메서드

    public static Holding create(
            Long roomParticipantId,
            String stockCode,
            String stockName,
            long quantity,
            BigDecimal price
    ) {
        return new Holding(
                roomParticipantId,
                stockCode,
                stockName,
                quantity,
                price
        );
    } // 외부에서 사용할 정적 팩토리 메서드

    public void buy(long buyQuantity, BigDecimal buyPrice) {
        validateQuantity(buyQuantity);
        validatePrice(buyPrice);

        BigDecimal previousAmount =
                this.avgPrice.multiply(BigDecimal.valueOf(this.quantity));

        BigDecimal additionalAmount =
                buyPrice.multiply(BigDecimal.valueOf(buyQuantity));

        long totalQuantity = Math.addExact(this.quantity, buyQuantity);

        this.avgPrice = previousAmount
                .add(additionalAmount)
                .divide(
                        BigDecimal.valueOf(totalQuantity),
                        2,
                        RoundingMode.HALF_UP
                );

        this.quantity = totalQuantity;
        this.updatedAt = LocalDateTime.now();
    } // 추가 매수 메서드

    public void sell(long sellQuantity) {
        validateQuantity(sellQuantity);

        if (this.quantity < sellQuantity) {
            throw new IllegalArgumentException("보유 수량이 부족합니다.");
        }

        this.quantity -= sellQuantity;
        this.updatedAt = LocalDateTime.now();
    } // 매도 메서드

    public boolean isEmpty() {
        return this.quantity == 0;
    } // 전량 매도 여부 확인 메서드

    private static void validateParticipant(Long roomParticipantId) {
        if (roomParticipantId == null || roomParticipantId <= 0) {
            throw new IllegalArgumentException("방 참가자 ID가 올바르지 않습니다.");
        }
    }

    private static void validateStock(String stockCode, String stockName) {
        if (stockCode == null || stockCode.isBlank()) {
            throw new IllegalArgumentException("종목 코드는 필수입니다.");
        }

        if (stockName == null || stockName.isBlank()) {
            throw new IllegalArgumentException("종목명은 필수입니다.");
        }
    }

    private static void validateQuantity(long quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("거래 수량은 1 이상이어야 합니다.");
        }
    }

    private static void validatePrice(BigDecimal price) {
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("거래 가격은 0보다 커야 합니다.");
        }
    } // 검증 메서드

} // Class