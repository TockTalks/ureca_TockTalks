package com.tocktalks.domain.room.repository;

import com.tocktalks.domain.room.entity.RoomParticipant;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RoomParticipantRepository
        extends JpaRepository<RoomParticipant, Long> {

    Optional<RoomParticipant>
    findByRoomIdAndMemberIdAndStatus(
            Long roomId,
            Long memberId,
            String status
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT rp
            FROM RoomParticipant rp
            WHERE rp.id = :roomParticipantId
              AND rp.memberId = :memberId
              AND rp.status = 'ACTIVE'
            """)
    Optional<RoomParticipant> findActiveForUpdate(
            @Param("roomParticipantId")
            Long roomParticipantId,

            @Param("memberId")
            Long memberId
    );

    long countByRoomIdAndStatus(
            Long roomId,
            String status
    );

    List<RoomParticipant> findByRoomIdAndStatus(
            Long roomId,
            String status
    );

    List<RoomParticipant> findByMemberIdAndStatus(
            Long memberId,
            String status
    );
}