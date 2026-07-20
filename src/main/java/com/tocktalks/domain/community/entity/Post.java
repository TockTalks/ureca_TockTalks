package com.tocktalks.domain.community.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
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

    @Column(name = "profit_amount", precision = 20, scale = 2)
    private BigDecimal profitAmount;

    @Column(name = "profit_rate", precision = 10, scale = 4)
    private BigDecimal profitRate;

    @Column(name = "like_count", nullable = false)
    private Integer likeCount;

    @Column(name = "comment_count", nullable = false)
    private Integer commentCount;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    private Post(Long memberId, String content, String stockCode,
                 Long transactionId, BigDecimal profitAmount, BigDecimal profitRate){
        this.memberId = memberId;
        this.content = content;
        this.stockCode = stockCode;
        this.transactionId = transactionId;
        this.profitAmount = profitAmount;
        this.profitRate = profitRate;
        this.likeCount = 0;
        this.commentCount = 0;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public static Post createTextPost(Long memberId, String content, String stockCode) {
        return new Post(memberId, content, stockCode, null, null, null);
    }

    public static Post createWithCertificate(Long memberId, String content, String stockCode,
                                     Long transactionId, BigDecimal profitAmount, BigDecimal profitRate) {
        return new Post(memberId, content, stockCode, transactionId, profitAmount, profitRate);
    }

    public void updateContent(String content, String stockCode){
        this.content = content;
        this.stockCode = stockCode;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isOwnedBy(Long memberId){
        return this.memberId.equals(memberId);
    }

    public void increaseLikeCount(){
        this.likeCount++;
    }

    public void decreaseLikeCount(){
        if(this.likeCount > 0) this.likeCount--;
    }

    public void increaseCommentCount(){
        this.commentCount++;
    }

    public void decreaseCommentCount(){
        if(this.commentCount > 0) this.commentCount--;
    }
}
