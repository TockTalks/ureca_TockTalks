package com.tocktalks.domain.room.repository;

import com.tocktalks.domain.room.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Long> {
    Optional<Room> findByIsDefaultTrue();

    Optional<Room> findByInviteCode(String inviteCode);

    List<Room> findByIsPublicTrueAndIsDefaultFalseAndStatus(String status);

    List<Room> findByStatusAndEndAtBefore(String status, LocalDateTime endAt);
}
