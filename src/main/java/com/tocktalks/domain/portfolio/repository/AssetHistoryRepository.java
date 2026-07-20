package com.tocktalks.domain.portfolio.repository;

import com.tocktalks.domain.portfolio.entity.AssetHistory;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssetHistoryRepository extends JpaRepository<AssetHistory, Long> {
    
    // 특정 참가자의 자산 기록을 날짜 오름차순(과거->최신)으로 조회
    List<AssetHistory> findAllByRoomParticipantIdOrderBySnapshotDateAsc(Long roomParticipantId);
    
    // 오늘 날짜로 이미 저장된 스냅샷이 있는지 확인 (중복 저장 방지용)
    boolean existsByRoomParticipantIdAndSnapshotDate(Long roomParticipantId, LocalDate snapshotDate);
}
