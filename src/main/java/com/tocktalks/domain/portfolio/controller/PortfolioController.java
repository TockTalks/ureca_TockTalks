package com.tocktalks.domain.portfolio.controller;

import com.tocktalks.domain.portfolio.dto.AssetHistoryResponse;
import com.tocktalks.domain.portfolio.service.PortfolioService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/portfolios")
@RequiredArgsConstructor
public class PortfolioController {
    private final PortfolioService portfolioService;
    
    //1. 자산 변동 히스토리 조회
    @GetMapping("/{roomParticipantId}/history")
    public ResponseEntity<List<AssetHistoryResponse>> getAssetHistory(@PathVariable Long roomParticipantId) {
        return ResponseEntity.ok(portfolioService.getAssetHistory(roomParticipantId));
    }
    
}
