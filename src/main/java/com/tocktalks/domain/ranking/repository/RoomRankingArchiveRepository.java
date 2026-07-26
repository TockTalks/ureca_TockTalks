package com.tocktalks.domain.ranking.repository;

import com.tocktalks.domain.ranking.entity.RoomRankingArchive;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRankingArchiveRepository extends JpaRepository<RoomRankingArchive, Long> {

    List<RoomRankingArchive> findByRoomIdOrderByFinalRankAsc(Long roomId);

    List<RoomRankingArchive> findByRoomIdOrderByFinalAssetDesc(Long roomId);

    // 정상적으로는 (roomId, memberId) 조합이 유일해야 하지만, 과거 동시성 버그로 중복
    // 저장된 데이터가 남아있을 수 있어 Optional 대신 List로 받아 방어적으로 처리한다.
    List<RoomRankingArchive> findByRoomIdAndMemberId(Long roomId, Long memberId);

    boolean existsByRoomId(Long roomId);

    List<RoomRankingArchive> findAllByOrderByFinalReturnRateDesc(Pageable pageable);

    List<RoomRankingArchive> findByMemberIdOrderByCreatedAtDesc(Long memberId);

    long countByRoomId(Long roomId);

}