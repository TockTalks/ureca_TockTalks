package com.tocktalks.domain.trade.repository;

import com.tocktalks.domain.trade.entity.HoldingArchive;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HoldingArchiveRepository extends JpaRepository<HoldingArchive, Long> {

    List<HoldingArchive> findByRoomParticipantId(Long roomParticipantId);
}