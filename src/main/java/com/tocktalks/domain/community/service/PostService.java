package com.tocktalks.domain.community.service;

import com.tocktalks.domain.community.dto.request.PostCreateRequest;
import com.tocktalks.domain.community.dto.request.PostUpdateRequest;
import com.tocktalks.domain.community.dto.response.PostResponse;
import com.tocktalks.domain.community.entity.Post;
import com.tocktalks.domain.community.entity.PostLike;
import com.tocktalks.domain.community.exception.CommunityErrorCode;
import com.tocktalks.domain.community.exception.CommunityException;
import com.tocktalks.domain.community.repository.PostLikeRepository;
import com.tocktalks.domain.community.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final TransactionCertificateProvider transactionCertificateProvider;

    @Transactional
    public PostResponse createPost(Long memberId, PostCreateRequest request) {
        Post post;

        if (request.transactionId() != null) {
            var snapshot = transactionCertificateProvider
                    .certifySellTransaction(request.transactionId(), memberId);

            post = Post.createWithCertificate(
                    memberId,
                    request.content(),
                    request.stockCode() != null ? request.stockCode() : snapshot.stockCode(),
                    request.transactionId(),
                    snapshot.profitAmount(),
                    snapshot.profitRate()
            );
        } else {
            post = Post.createTextPost(memberId, request.content(), request.stockCode());
        }

        Post saved = postRepository.save(post);
        return PostResponse.of(saved, false);
    }

    public PostResponse getPost(Long postId, Long viewerId) {
        Post post = getPostOrThrow(postId);
        boolean likedByMe = postLikeRepository.existsByPostIdAndMemberId(postId, viewerId);
        return PostResponse.of(post, likedByMe);
    }

    public Page<PostResponse> getPosts(String stockCode, Long viewerId, Pageable pageable) {
        Page<Post> posts = (stockCode == null)
                ? postRepository.findAllByOrderByCreatedAtDesc(pageable)
                : postRepository.findByStockCodeOrderByCreatedAtDesc(stockCode, pageable);

        List<Long> postIds = posts.getContent().stream().map(Post::getId).toList();
        Map<Long, Boolean> likedMap = postLikeRepository
                .findByMemberIdAndPostIdIn(viewerId, postIds).stream()
                .collect(Collectors.toMap(PostLike::getPostId, like -> true));

        return posts.map(post -> PostResponse.of(post, likedMap.getOrDefault(post.getId(), false)));
    }

    @Transactional
    public PostResponse updatePost(Long postId, Long memberId, PostUpdateRequest request){
        Post post = getOwnedPostOrThrow(postId, memberId);
        post.updateContent(request.content(), request.stockCode());
        return PostResponse.of(post, postLikeRepository.existsByPostIdAndMemberId(postId, memberId));
    }

    @Transactional
    public void deletePost(Long postId, Long memberId){
        Post post = getOwnedPostOrThrow(postId, memberId);
        postRepository.delete(post);
    }

    private Post getPostOrThrow(Long postId){
        return postRepository.findById(postId)
                .orElseThrow(() -> new CommunityException(CommunityErrorCode.POST_NOT_FOUND));
    }

    private Post getOwnedPostOrThrow(Long postId, Long memberId){
        Post post = getPostOrThrow(postId);
        if(!post.isOwnedBy(memberId)){
            throw new CommunityException(CommunityErrorCode.POST_ACCESS_DENIED);
        }
        return post;
    }

    @Transactional
    public void deletePostByAdmin(Long postId){
        Post post = getPostOrThrow(postId);
        postRepository.delete(post);
    }

}