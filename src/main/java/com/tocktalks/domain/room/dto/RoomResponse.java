package com.tocktalks.domain.room.dto;

import com.tocktalks.domain.room.entity.Room;

import java.time.LocalDateTime;

public record RoomResponse(
        Long id,
        String name,
        boolean isDefault,
        boolean isPublic,
        Long seedMoney,
        String inviteCode,
        Integer maxParticipants,
        LocalDateTime startAt,
        LocalDateTime endAt,
        String status,
        long participantCount
) {
    public static RoomResponse of(Room room, long participantCount) {
        return of(room, participantCount, true);
    }

    public static RoomResponse of(Room room, long participantCount, boolean includeInviteCode) {
        return new RoomResponse(
                room.getId(),
                room.getName(),
                room.isDefault(),
                room.isPublic(),
                room.getSeedMoney(),
                includeInviteCode ? room.getInviteCode() : null,
                room.getMaxParticipants(),
                room.getStartAt(),
                room.getEndAt(),
                room.getStatus(),
                participantCount
        );
    }
}
