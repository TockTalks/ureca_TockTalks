package com.tocktalks.domain.ranking.controller;

import com.tocktalks.domain.ranking.dto.response.RankingArchiveResponse;
import com.tocktalks.domain.ranking.dto.response.RankingListResponse;
import com.tocktalks.domain.ranking.service.RankingService;
import com.tocktalks.domain.ranking.type.RankingType;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rooms/{roomId}/rankings")
@RequiredArgsConstructor
public class RankingController {

    private final RankingService rankingService;

    @GetMapping
    public RankingListResponse getRanking(
            @PathVariable Long roomId,
            @AuthenticationPrincipal Long memberId,
            @RequestParam(defaultValue = "RETURN_RATE") RankingType type,
            @RequestParam(defaultValue = "10") int topN
    ){
        return rankingService.getRanking(roomId, memberId, type, topN);
    }

    @GetMapping("/final")
    public List<RankingArchiveResponse> getFinalRanking(
            @PathVariable Long roomId,
            @RequestParam(defaultValue = "RETURN_RATE") RankingType type
    ){
        return rankingService.getFinalRanking(roomId, type);
    }

}
