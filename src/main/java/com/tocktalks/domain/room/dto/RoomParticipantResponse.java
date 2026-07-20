package com.tocktalks.domain.room.dto;

import com.tocktalks.domain.room.entity.RoomParticipant;

import java.time.LocalDateTime;

public record RoomParticipantResponse(
        Long id,
        Long roomId,
        Long memberId,
        Long balance,
        Long initialSeedMoney,
        String status,
        LocalDateTime joinedAt,
        LocalDateTime endedAt
) {
    public static RoomParticipantResponse of(RoomParticipant participant) {
        return new RoomParticipantResponse(
                participant.getId(),
                participant.getRoomId(),
                participant.getMemberId(),
                participant.getBalance(),
                participant.getInitialSeedMoney(),
                participant.getStatus(),
                participant.getJoinedAt(),
                participant.getEndedAt()
        );
    }
}
