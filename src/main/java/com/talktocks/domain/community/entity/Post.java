package com.talktocks.domain.community.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "post")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Lob
    @Column(nullable = false)
    private String content;

    @Column(name = "stock_code", length = 20)
    private String stockCode;

    // 매도 체결 완료된 거래만 첨부 가능. 손익 스냅샷 대체용(profit_amount/profit_rate 고정값)
    @Column(name = "transaction_id")
    private Long transactionId;

    @Column(name = "like_count", nullable = false)
    private Integer likeCount;

    @Column(name = "comment_count", nullable = false)
    private Integer commentCount;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
