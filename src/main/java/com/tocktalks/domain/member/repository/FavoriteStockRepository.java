package com.tocktalks.domain.member.repository;

import com.tocktalks.domain.member.entity.FavoriteStock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FavoriteStockRepository extends JpaRepository<FavoriteStock, Long> {

    List<FavoriteStock> findByMemberIdOrderByCreatedAtAsc(Long memberId);

    boolean existsByMemberIdAndStockCode(Long memberId, String stockCode);

    void deleteByMemberIdAndStockCode(Long memberId, String stockCode);
}