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

    // 상태 무관하게 한 번이라도 참가한 적 있는지 확인 (한 번 나간 방은 재입장 불가 정책)
    boolean existsByRoomIdAndMemberId(
            Long roomId,
            Long memberId
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
