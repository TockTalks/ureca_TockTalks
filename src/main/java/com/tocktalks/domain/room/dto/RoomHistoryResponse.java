package com.tocktalks.domain.room.dto;

import com.tocktalks.domain.ranking.entity.RoomRankingArchive;
import com.tocktalks.domain.room.entity.Room;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record RoomHistoryResponse(
        Long roomId,
        String roomName,
        LocalDateTime endAt,
        Integer finalRank,
        Long finalAsset,
        BigDecimal finalReturnRate,
        long participantCount
) {
    public static RoomHistoryResponse of(Room room, RoomRankingArchive archive, long participantCount) {
        return new RoomHistoryResponse(
                room.getId(),
                room.getName(),
                room.getEndAt(),
                archive.getFinalRank(),
                archive.getFinalAsset(),
                archive.getFinalReturnRate(),
                participantCount
        );
    }
}
