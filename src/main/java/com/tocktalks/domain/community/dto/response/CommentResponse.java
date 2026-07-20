package com.tocktalks.domain.community.dto.response;

import com.tocktalks.domain.community.entity.Comment;

import java.time.LocalDateTime;

public record CommentResponse(
        Long id,
        Long postId,
        Long memberId,
        String content,
        int likeCount,
        boolean likedByMe,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static CommentResponse of(Comment comment, boolean likedByMe){
        return new CommentResponse(
                comment.getId(),
                comment.getPostId(),
                comment.getMemberId(),
                comment.getContent(),
                comment.getLikeCount(),
                likedByMe,
                comment.getCreatedAt(),
                comment.getUpdatedAt()
        );
    }
}
