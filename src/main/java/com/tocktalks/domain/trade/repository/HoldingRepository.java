package com.tocktalks.domain.trade.repository;

import com.tocktalks.domain.trade.entity.Holding;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface HoldingRepository extends JpaRepository<Holding, Long> {

    Optional<Holding> findByRoomParticipantIdAndStockCode(
            Long roomParticipantId,
            String stockCode
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT h
            FROM Holding h
            WHERE h.roomParticipantId = :roomParticipantId
              AND h.stockCode = :stockCode
            """)
    Optional<Holding> findForUpdate(
            @Param("roomParticipantId") Long roomParticipantId,
            @Param("stockCode") String stockCode
    );

    List<Holding> findAllByRoomParticipantId(
            Long roomParticipantId
    );
}