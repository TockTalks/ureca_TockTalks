package com.tocktalks.domain.ranking.dto.response;

import com.tocktalks.domain.ranking.entity.RoomRankingArchive;

import java.math.BigDecimal;

public record RankingArchiveResponse (
        Long memberId,
        Long finalAsset,
        BigDecimal finalReturnRate,
        Integer finalRank
) {
    public static RankingArchiveResponse from(RoomRankingArchive entity){
        return new RankingArchiveResponse(
            entity.getMemberId(),
            entity.getFinalAsset(),
            entity.getFinalReturnRate(),
            entity.getFinalRank()
        );
    }
}
