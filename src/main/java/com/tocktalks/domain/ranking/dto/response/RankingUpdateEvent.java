package com.tocktalks.domain.ranking.dto.response;

public record RankingUpdateEvent (
    Long memberId,
    Integer newRank
) {}
