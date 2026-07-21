package com.tocktalks.domain.ranking.dto.response;

import java.util.List;

public record RankingUpdateEvent (
    Long memberId,
    Integer newRank,
    List<RankingDto> topN
) {}
