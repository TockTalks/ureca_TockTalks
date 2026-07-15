package com.tocktalks.domain.ranking.repository;

import com.tocktalks.domain.ranking.entity.RoomRankingArchive;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRankingArchiveRepository extends JpaRepository<RoomRankingArchive, Long> {
}
