package com.tocktalks.domain.room.repository;

import com.tocktalks.domain.room.entity.RoomParticipant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoomParticipantRepository extends JpaRepository<RoomParticipant, Long> {
    Optional<RoomParticipant> findByRoomIdAndMemberIdAndStatus(Long roomId, Long memberId, String status);

    long countByRoomIdAndStatus(Long roomId, String status);

    List<RoomParticipant> findByRoomIdAndStatus(Long roomId, String status);

    List<RoomParticipant> findByMemberIdAndStatus(Long memberId, String status);
}
