package com.tocktalks.domain.portfolio.controller;

import com.tocktalks.domain.portfolio.dto.AssetHistoryResponse;
import com.tocktalks.domain.portfolio.dto.PortfolioBalanceResponse;
import com.tocktalks.domain.portfolio.dto.PortfolioDetailResponse;
import com.tocktalks.domain.portfolio.dto.PortfolioSummaryResponse;
import com.tocktalks.domain.portfolio.service.PortfolioService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/portfolios")
@RequiredArgsConstructor
public class PortfolioController {
    private final PortfolioService portfolioService;

    //내 포트폴리오 목록 조회
    @GetMapping
    public ResponseEntity<List<PortfolioSummaryResponse>> getPortfolios(
            Authentication authentication
    ) {
        Long memberId = extractMemberId(authentication);

        return ResponseEntity.ok(portfolioService.getPortfolios(memberId));
    }

    //포트폴리오 상세 조회
    @GetMapping("/{roomParticipantId}")
    public ResponseEntity<PortfolioDetailResponse> getPortfolioDetail(
            Authentication authentication,
            @PathVariable Long roomParticipantId
    ) {
        Long memberId = extractMemberId(authentication);

        return ResponseEntity.ok(portfolioService.getPortfolioDetail(memberId, roomParticipantId));
    }

    // 현금 잔고만 경량 조회 (시세 조회 없이 즉시 응답)
    @GetMapping("/{roomParticipantId}/balance")
    public ResponseEntity<PortfolioBalanceResponse> getBalance(
            Authentication authentication,
            @PathVariable Long roomParticipantId
    ) {
        Long memberId = extractMemberId(authentication);

        return ResponseEntity.ok(portfolioService.getBalance(memberId, roomParticipantId));
    }

    // 자산 변동 히스토리 조회
    @GetMapping("/{roomParticipantId}/history")
    public ResponseEntity<List<AssetHistoryResponse>> getAssetHistory(
        Authentication authentication,
        @PathVariable Long roomParticipantId
    ) {
        Long memberId = extractMemberId(authentication);
        
        return ResponseEntity.ok(portfolioService.getAssetHistory(memberId, roomParticipantId));
    }
    
    private Long extractMemberId(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof Long memberId)) {
            throw new IllegalArgumentException("인증된 사용자가 아닙니다.");
        }
        return memberId;
    }
}
