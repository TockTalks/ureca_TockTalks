package com.tocktalks.domain.trade.repository;

import com.tocktalks.domain.trade.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository
        extends JpaRepository<Transaction, Long> {

    Page<Transaction> findAllByRoomParticipantIdOrderByExecutedAtDesc(
            Long roomParticipantId,
            Pageable pageable
    );

    long countByExecutedAtBetween(LocalDateTime start, LocalDateTime end);

    @Query("""
            SELECT COALESCE(SUM(t.price * t.quantity), 0)
            FROM Transaction t
            WHERE t.executedAt >= :start AND t.executedAt < :end
            """)
    BigDecimal sumAmountBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query(
            value = """
                    SELECT t.*
                    FROM transaction t
                    JOIN room_participant rp
                      ON rp.id = t.room_participant_id
                    WHERE t.id = :transactionId
                      AND rp.member_id = :memberId
                    """,
            nativeQuery = true
    )
    Optional<Transaction> findOwnedByIdAndMemberId(
            @Param("transactionId") Long transactionId,
            @Param("memberId") Long memberId
    );

    @Query("""
            SELECT t.stockCode, t.stockName, COUNT(t)
            FROM Transaction t
            WHERE t.executedAt BETWEEN :start AND :end
            GROUP BY t.stockCode, t.stockName
            ORDER BY COUNT(t) DESC
            """)
    List<Object[]> findTopTradedStocks(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            Pageable pageable
    );

}