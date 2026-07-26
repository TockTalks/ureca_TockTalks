package com.tocktalks.domain.room.repository;

import com.tocktalks.domain.room.entity.RoomParticipant;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
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

    boolean existsByRoomIdAndMemberIdAndStatus(
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

    long countByRoomId(Long roomId);

    // 종료된 방의 실제 참가자 수: 시작 전(모집중)에 들어왔다 나간 기록은 게임에 참여한 적이
    // 없으므로 제외하고, 회원 단위로 센다(재입장으로 같은 회원의 row가 여러 개일 수 있음).
    @Query("""
            SELECT COUNT(DISTINCT rp.memberId)
            FROM RoomParticipant rp
            WHERE rp.roomId = :roomId
              AND (rp.endedAt IS NULL OR :startAt IS NULL OR rp.endedAt >= :startAt)
            """)
    long countRealParticipantsByRoomId(
            @Param("roomId") Long roomId,
            @Param("startAt") LocalDateTime startAt
    );

    List<RoomParticipant> findByRoomIdAndStatus(
            Long roomId,
            String status
    );

    List<RoomParticipant> findByMemberIdAndStatus(
            Long memberId,
            String status
    );

    List<RoomParticipant> findByMemberId(Long memberId);

    List<RoomParticipant> findByStatus(String status);

    /**
     * 랭킹 제외 로직 도입 전에 탈퇴해 ACTIVE 참가가 남은 회원을 자동 보정한다.
     */
    @Query("""
            SELECT rp
            FROM RoomParticipant rp
            WHERE rp.status = 'ACTIVE'
              AND rp.memberId IN (
                  SELECT m.id
                  FROM Member m
                  WHERE m.status = 'withdrawn'
              )
            """)
    List<RoomParticipant> findActiveParticipantsOfWithdrawnMembers();

    long countByStatus(String status);

    void deleteByRoomId(Long roomId);
}
