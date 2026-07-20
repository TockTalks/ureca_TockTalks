package com.tocktalks.domain.community.controller;

import com.tocktalks.domain.community.dto.request.PostCreateRequest;
import com.tocktalks.domain.community.dto.request.PostUpdateRequest;
import com.tocktalks.domain.community.dto.response.PostResponse;
import com.tocktalks.domain.community.service.PostLikeService;
import com.tocktalks.domain.community.service.PostService;
import com.tocktalks.global.security.LoginMemberId;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final PostLikeService postLikeService;

    @PostMapping
    public ResponseEntity<PostResponse> createPost(
            @LoginMemberId Long memberId,
            @Valid @RequestBody PostCreateRequest request
    ){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(postService.createPost(memberId, request));
    }

    @GetMapping
    public ResponseEntity<Page<PostResponse>> getPosts(
            @LoginMemberId Long memberId,
            @RequestParam(required = false) String stockCode,
            Pageable pageable
    ){
        return ResponseEntity.ok(postService.getPosts(stockCode, memberId, pageable));
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostResponse> getPost(
            @LoginMemberId Long memberId,
            @PathVariable Long postId
    ) {
        return ResponseEntity.ok(postService.getPost(postId, memberId));
    }

    @PatchMapping("/{postId}")
    public ResponseEntity<PostResponse> updatePost(
            @LoginMemberId Long memberId,
            @PathVariable Long postId,
            @Valid @RequestBody PostUpdateRequest request
    ) {
        return ResponseEntity.ok(postService.updatePost(postId, memberId, request));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(
            @LoginMemberId Long memberId,
            @PathVariable Long postId
    ) {
        postService.deletePost(postId, memberId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{postId}/like")
    public ResponseEntity<Boolean> toggleLike(
            @PathVariable Long postId,
            @LoginMemberId Long memberId
    ){
        return ResponseEntity.ok(postLikeService.toggleLike(postId, memberId));
    }

}
