package com.tocktalks.domain.community.controller;

import com.tocktalks.domain.community.dto.request.CommentCreateRequest;
import com.tocktalks.domain.community.dto.request.CommentUpdateRequest;
import com.tocktalks.domain.community.dto.response.CommentResponse;
import com.tocktalks.domain.community.service.CommentLikeService;
import com.tocktalks.domain.community.service.CommentService;
import com.tocktalks.global.security.LoginMemberId;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts/{postId}/comments")
public class CommentController {

    private final CommentService commentService;
    private final CommentLikeService commentLikeService;

    @PostMapping
    public ResponseEntity<CommentResponse> createCommet(
            @PathVariable Long postId,
            @LoginMemberId Long memberId,
            @Valid @RequestBody CommentCreateRequest request
    ){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(commentService.createComment(postId, memberId, request));
    }

    @GetMapping
    public ResponseEntity<Page<CommentResponse>> getComments(
            @PathVariable Long postId,
            @LoginMemberId Long memberId,
            @PageableDefault(size = 20) Pageable pageable
    ){
        return ResponseEntity.ok(commentService.getComments(postId, memberId, pageable));
    }

    @PatchMapping("/{commentId}")
    public ResponseEntity<CommentResponse> updateComment(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @LoginMemberId Long memberId,
            @Valid @RequestBody CommentUpdateRequest request
    ){
        return ResponseEntity.ok(commentService.updateComment(commentId, memberId, request));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @LoginMemberId Long memberId
    ){
        commentService.deleteComment(commentId, memberId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{commentId}/like")
    public ResponseEntity<Boolean> toggleLike(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @LoginMemberId Long memberId
    ){
        return ResponseEntity.ok(commentLikeService.toggleLike(commentId, memberId));
    }
}
