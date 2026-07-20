package com.tocktalks.domain.community.service;

import com.tocktalks.domain.community.entity.Post;
import com.tocktalks.domain.community.entity.PostLike;
import com.tocktalks.domain.community.exception.CommunityErrorCode;
import com.tocktalks.domain.community.exception.CommunityException;
import com.tocktalks.domain.community.repository.PostLikeRepository;
import com.tocktalks.domain.community.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostLikeService {

    private final PostLikeRepository postLikeRepository;
    private final PostRepository postRepository;

    @Transactional
    public boolean toggleLike(Long postId, Long memberId){
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CommunityException(CommunityErrorCode.POST_NOT_FOUND));
        return postLikeRepository.findByPostIdAndMemberId(postId, memberId)
                .map(like -> {
                    postLikeRepository.delete(like);
                    post.decreaseLikeCount();
                    return false;
                })
                .orElseGet(() -> {
                    postLikeRepository.save(PostLike.create(postId, memberId));
                    post.increaseLikeCount();
                    return true;
                });
    }
}
