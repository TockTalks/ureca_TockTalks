package com.tocktalks.domain.room.dto;

public record RoomRankingResponse(
        int rank,
        Long memberId,
        String nickname,
        Long balance
) {
}
