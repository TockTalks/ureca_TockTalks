package com.tocktalks.domain.community.dto.response;

import com.tocktalks.domain.community.entity.Post;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PostResponse(
        Long id,
        Long memberId,
        String nickname,
        String content,
        String stockCode,
        String stockName,
        Long quantity,
        Long transactionId,
        BigDecimal profitAmount,
        BigDecimal profitRate,
        boolean hasCertificate,
        Integer likeCount,
        Integer commentCount,
        boolean likedByMe,
        LocalDateTime createAt,
        LocalDateTime updateAt
) {
    public static PostResponse of(Post post, boolean likedByMe, String nickname){
        return new PostResponse(
                post.getId(),
                post.getMemberId(),
                nickname,
                post.getContent(),
                post.getStockCode(),
                post.getStockName(),
                post.getQuantity(),
                post.getTransactionId(),
                post.getProfitAmount(),
                post.getProfitRate(),
                post.getTransactionId() != null,
                post.getLikeCount(),
                post.getCommentCount(),
                likedByMe,
                post.getCreatedAt(),
                post.getUpdatedAt()
        );
    }
}
