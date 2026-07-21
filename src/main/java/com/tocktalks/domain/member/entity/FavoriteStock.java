package com.tocktalks.domain.member.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "favorite_stock")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FavoriteStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "stock_code", nullable = false, length = 20)
    private String stockCode;

    @Column(name = "stock_name", nullable = false, length = 100)
    private String stockName;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public static FavoriteStock of(Long memberId, String stockCode, String stockName) {
        FavoriteStock favoriteStock = new FavoriteStock();
        favoriteStock.memberId = memberId;
        favoriteStock.stockCode = stockCode;
        favoriteStock.stockName = stockName;
        favoriteStock.createdAt = LocalDateTime.now();

        return favoriteStock;
    }
}
