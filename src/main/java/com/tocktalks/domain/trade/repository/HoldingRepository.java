package com.tocktalks.domain.trade.repository;

import com.tocktalks.domain.trade.entity.Holding;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface HoldingRepository
        extends JpaRepository<Holding, Long> {

    Optional<Holding>
    findByRoomParticipantIdAndStockCode(
            Long roomParticipantId,
            String stockCode
    );

    List<Holding> findAllByRoomParticipantId(
            Long roomParticipantId
    );

    List<Holding> findAllByRoomParticipantIdIn(List<Long> roomParticipantIds);

    //현재 보유중인 모든 종목 코드 - 포트폴리오에서 사용
    @Query("SELECT DISTINCT h.stockCode FROM Holding h")
    List<String> findDistinctStockCode();
}