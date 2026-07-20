package com.tocktalks.domain.community.repository;

import com.tocktalks.domain.community.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    Page<Post> findAllByOrderByCreatedAtDesc(Pageable pageable);

    Page<Post> findByStockCodeOrderByCreatedAtDesc(String stockCode, Pageable pageable);

    Optional<Post> findByAndMemberId(Long id, Long memberId);
}
