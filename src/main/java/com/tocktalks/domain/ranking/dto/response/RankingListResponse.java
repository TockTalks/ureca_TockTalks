package com.tocktalks.domain.ranking.dto.response;

import org.hibernate.boot.models.MemberResolutionException;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

public record RankingListResponse (
        List<RankingDto> topN,
        RankingDto myRank
) {}
