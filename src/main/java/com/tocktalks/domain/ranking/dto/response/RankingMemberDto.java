package com.tocktalks.domain.ranking.dto.response;

public record RankingMemberDto(
        Integer rank,
        Long memberId,
        String nickname,
        Long balance
){}
