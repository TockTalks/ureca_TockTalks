package com.tocktalks.domain.community.repository;

import com.tocktalks.domain.community.entity.CommentLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {

    boolean existsByCommentIdAndMemberId(Long commentId, Long memberId);

    Optional<CommentLike> findByCommentIdAndMemberId(Long commentId, Long memberId);

    List<CommentLike> findByMemberIdAndCommentIdIn(Long memberId, List<Long> commentIds);

    void deleteByCommentIdAndMemberId(Long commentId, Long memberId);

}
