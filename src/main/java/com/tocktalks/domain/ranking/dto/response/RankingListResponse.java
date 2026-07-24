package com.tocktalks.domain.ranking.dto.response;

import java.util.List;

public record RankingListResponse (
        List<RankingDto> topN,
        RankingDto myRank
) {}
