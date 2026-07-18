package com.tocktalks.domain.community.repository;

import com.tocktalks.domain.community.entity.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    Optional<PostLike> findByPostIdAndMemberId(Long postId, Long memberId);

    boolean existsByPostIdAndMemberId(Long postId, Long memberId);

    List<PostLike> findByMemberIdAndPostIdIn(Long memberId, List<Long> postIds);
}
