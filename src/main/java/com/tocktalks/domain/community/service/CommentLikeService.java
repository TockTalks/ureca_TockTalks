package com.tocktalks.domain.community.service;

import com.tocktalks.domain.community.entity.Comment;
import com.tocktalks.domain.community.entity.CommentLike;
import com.tocktalks.domain.community.exception.CommunityErrorCode;
import com.tocktalks.domain.community.exception.CommunityException;
import com.tocktalks.domain.community.repository.CommentLikeRepository;
import com.tocktalks.domain.community.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentLikeService {

    private final CommentLikeRepository commentLikeRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public boolean toggleLike(Long commentId, Long memberId){
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommunityException(CommunityErrorCode.COMMENT_NOT_FOUND));

        return commentLikeRepository.findByCommentIdAndMemberId(commentId, memberId)
                .map(like -> {
                    commentLikeRepository.delete(like);
                    comment.decreaseLikeCount();
                    return false;
                })
                .orElseGet(() -> {
                    commentLikeRepository.save(CommentLike.create(commentId, memberId));
                    comment.increaseLikeCount();
                    return true;
                });
    }
}
