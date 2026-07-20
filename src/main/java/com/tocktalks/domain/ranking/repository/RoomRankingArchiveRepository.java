package com.tocktalks.domain.ranking.repository;

import com.tocktalks.domain.ranking.entity.RoomRankingArchive;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRankingArchiveRepository extends JpaRepository<RoomRankingArchive, Long> {
    //테스트
    List<RoomRankingArchive> findByRoomIdOrderByFinalRankAsc(Long roomId);

    List<RoomRankingArchive> findByRoomIdOrderByFinalAssetDesc(Long roomId);

    Optional<RoomRankingArchive> findByRoomIdAndMemberId(Long roomId, Long memberId);

    boolean existsByRoomId(Long roomId);

}
