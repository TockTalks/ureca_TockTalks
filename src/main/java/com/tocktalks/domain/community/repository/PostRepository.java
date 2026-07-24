package com.tocktalks.domain.community.repository;

import com.tocktalks.domain.community.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    Page<Post> findAllByOrderByCreatedAtDesc(Pageable pageable);

    Page<Post> findByStockCodeOrderByCreatedAtDesc(String stockCode, Pageable pageable);

    Optional<Post> findByAndMemberId(Long id, Long memberId);

    long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    List<Post> findAllByOrderByLikeCountDescCommentCountDesc(Pageable pageable);

    @Query("SELECT p.createdAt FROM Post p WHERE p.createdAt BETWEEN :start AND :end")
    List<LocalDateTime> findCreatedAtBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}