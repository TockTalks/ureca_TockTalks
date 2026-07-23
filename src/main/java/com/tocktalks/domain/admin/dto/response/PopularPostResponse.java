package com.tocktalks.domain.admin.dto.response;

public record PopularPostResponse(
        Long postId,
        String content,
        int likeCount,
        int commentCount
) {
}