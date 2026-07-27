package com.tocktalks.domain.room.repository;

import com.tocktalks.domain.room.entity.Room;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Long> {
    Optional<Room> findByIsDefaultTrue();

    // 방 종료 아카이브 저장, 참가 등 동시성이 걸리는 처리를 방 단위로 직렬화하기 위한 비관적 락 조회.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT r FROM Room r WHERE r.id = :roomId")
    Optional<Room> findByIdForUpdate(@Param("roomId") Long roomId);

    Optional<Room> findByInviteCode(String inviteCode);

    List<Room> findByIsPublicTrueAndIsDefaultFalseAndStatus(String status);

    List<Room> findByStatusAndEndAtBefore(String status, LocalDateTime endAt);

    List<Room> findByStatusAndStartAtBefore(String status, LocalDateTime startAt);

    List<Room> findByStatus(String status);

    long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    long countByStatus(String status);
}
