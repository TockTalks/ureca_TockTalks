package com.tocktalks.domain.trade.repository;

import com.tocktalks.domain.trade.entity.Holding;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HoldingRepository extends JpaRepository<Holding, Long> {

    Optional<Holding> findByRoomParticipantIdAndStockCode(
            Long roomParticipantId,
            String stockCode
    );

    List<Holding> findAllByRoomParticipantId(
            Long roomParticipantId
    );
}