package com.tocktalks.domain.community.service;

import com.tocktalks.domain.community.dto.request.CommentCreateRequest;
import com.tocktalks.domain.community.dto.request.CommentUpdateRequest;
import com.tocktalks.domain.community.dto.response.CommentResponse;
import com.tocktalks.domain.community.entity.Comment;
import com.tocktalks.domain.community.entity.CommentLike;
import com.tocktalks.domain.community.entity.Post;
import com.tocktalks.domain.community.exception.CommunityErrorCode;
import com.tocktalks.domain.community.exception.CommunityException;
import com.tocktalks.domain.community.repository.CommentLikeRepository;
import com.tocktalks.domain.community.repository.CommentRepository;
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
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final PostRepository postRepository;

    @Transactional
    public CommentResponse createComment(Long postId, Long memberId, CommentCreateRequest request){
        Post post = getPostOrThrow(postId);

        Comment saved = commentRepository.save(Comment.create(postId, memberId, request.content()));
        post.increaseCommentCount();

        return CommentResponse.of(saved, false);
    }

    public Page<CommentResponse> getComments(Long postId, Long viewerId, Pageable pageable){
        Page<Comment> comments = commentRepository.findByPostIdOrderByCreatedAyAsc(postId, pageable);

        List<Long> commentIds = comments.getContent().stream().map(Comment::getId).toList();
        Map<Long, Boolean> likedMap = commentLikeRepository
                .findByMemberIdAndCommentIdIn(viewerId, commentIds).stream()
                .collect(Collectors.toMap(CommentLike::getCommentId, like -> true));

        return comments.map(c -> CommentResponse.of(c, likedMap.getOrDefault(c.getId(), false)));
    }

    @Transactional
    public CommentResponse updateComment(Long commentId, Long memberId, CommentUpdateRequest request){
        Comment comment = getOwnedCommentOrThrow(commentId, memberId);
        comment.updateContent(request.content());

        boolean likedByMe = commentLikeRepository.existsByCommentIdAndMemberId(commentId, memberId);
        return CommentResponse.of(comment, likedByMe);
    }

    @Transactional
    public void deleteComment(Long commentId, Long memberId){
        Comment comment = getOwnedCommentOrThrow(commentId, memberId);
        Post post = getPostOrThrow(comment.getPostId());

        commentRepository.delete(comment);
        post.decreaseCommentCount();
    }

    private Comment getCommentOrThrow(Long commentId){
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new CommunityException(CommunityErrorCode.COMMENT_NOT_FOUND));
    }

    private Comment getOwnedCommentOrThrow(Long commentId, Long memberId){
        Comment comment = getCommentOrThrow(commentId);
        if(!comment.isOwnedBy(memberId)){
            throw new CommunityException(CommunityErrorCode.COMMENT_ACCESS_DENIED);
        }
        return comment;
    }

    private Post getPostOrThrow(Long postId){
        return postRepository.findById(postId)
                .orElseThrow(() -> new CommunityException(CommunityErrorCode.POST_NOT_FOUND));
    }

}
