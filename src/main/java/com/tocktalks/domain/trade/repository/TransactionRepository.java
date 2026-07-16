package com.tocktalks.domain.trade.repository;

import com.tocktalks.domain.trade.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository
        extends JpaRepository<Transaction, Long> {

    Page<Transaction> findAllByRoomParticipantIdOrderByExecutedAtDesc(
            Long roomParticipantId,
            Pageable pageable
    );
}