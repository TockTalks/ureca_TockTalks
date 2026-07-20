package com.tocktalks.domain.ranking.dto.response;

import org.springframework.data.redis.core.ZSetOperations;

public record RankingDto (
        Long memberId,
        Double score,
        Integer rank
) {
    public static RankingDto of(ZSetOperations.TypedTuple<String> tuple, int rank){
        return new RankingDto(
                Long.valueOf(tuple.getValue()),
                tuple.getScore(),
                rank
        );
    }
}
