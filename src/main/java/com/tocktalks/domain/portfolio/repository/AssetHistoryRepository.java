package com.tocktalks.domain.portfolio.repository;

import com.tocktalks.domain.portfolio.entity.AssetHistory;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssetHistoryRepository extends JpaRepository<AssetHistory, Long> {

    // 특정 참가자의 자산 기록을 날짜 오름차순(과거->최신)으로 조회
    List<AssetHistory> findAllByRoomParticipantIdOrderByRecordedAtDesc(Long roomParticipantId);

    //기본방용 최신 4주만 기록
    List<AssetHistory> findByRoomParticipantIdAndRecordedAtAfterOrderByRecordedAtDesc(Long roomParticipantId, LocalDateTime after);
}
