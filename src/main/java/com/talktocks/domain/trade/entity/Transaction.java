package com.talktocks.domain.trade.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
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

    @Column(nullable = false, length = 10)
    private String type; // BUY / SELL

    @Column(nullable = false)
    private Long quantity;

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal price;

    @Column(name = "profit_amount", precision = 18, scale = 2)
    private BigDecimal profitAmount; // 매도 시에만

    @Column(name = "profit_rate", precision = 9, scale = 4)
    private BigDecimal profitRate; // 매도 시에만

    @Column(name = "executed_at", nullable = false)
    private LocalDateTime executedAt;
}
