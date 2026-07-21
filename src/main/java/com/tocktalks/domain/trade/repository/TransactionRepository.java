package com.tocktalks.domain.trade.repository;

import com.tocktalks.domain.trade.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TransactionRepository
        extends JpaRepository<Transaction, Long> {

    Page<Transaction> findAllByRoomParticipantIdOrderByExecutedAtDesc(
            Long roomParticipantId,
            Pageable pageable
    );

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
}