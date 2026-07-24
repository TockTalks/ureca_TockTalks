package com.tocktalks.domain.ranking.repository;

import com.tocktalks.domain.ranking.entity.RoomRankingArchive;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRankingArchiveRepository extends JpaRepository<RoomRankingArchive, Long> {

    List<RoomRankingArchive> findByRoomIdOrderByFinalRankAsc(Long roomId);

    List<RoomRankingArchive> findByRoomIdOrderByFinalAssetDesc(Long roomId);

    Optional<RoomRankingArchive> findByRoomIdAndMemberId(Long roomId, Long memberId);

    boolean existsByRoomId(Long roomId);

    List<RoomRankingArchive> findAllByOrderByFinalReturnRateDesc(Pageable pageable);

    List<RoomRankingArchive> findByMemberIdOrderByCreatedAtDesc(Long memberId);

    long countByRoomId(Long roomId);
}