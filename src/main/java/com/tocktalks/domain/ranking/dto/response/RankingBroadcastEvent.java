package com.tocktalks.domain.ranking.dto.response;

import java.util.List;

public record RankingBroadcastEvent(
        List<RankingMemberDto> ranking
) {}
