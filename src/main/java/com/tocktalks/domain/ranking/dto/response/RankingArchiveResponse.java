package com.tocktalks.domain.ranking.dto.response;

import com.tocktalks.domain.ranking.entity.RoomRankingArchive;

import java.math.BigDecimal;

public record RankingArchiveResponse (
        Long memberId,
        String nickname,
        Long finalAsset,
        BigDecimal finalReturnRate,
        Integer finalRank
) {
    public static RankingArchiveResponse from(RoomRankingArchive entity, String nickname){
        return new RankingArchiveResponse(
            entity.getMemberId(),
            nickname,
            entity.getFinalAsset(),
            entity.getFinalReturnRate(),
            entity.getFinalRank()
        );
    }
}
