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
    
    //2. 보유 종목 및 평가손익 조회 (trade 연동 전까지 사용할 임시)
    @GetMapping("/{roomParticipantId}/history")
    public ResponseEntity<?> getHoldingMock(@PathVariable Long roomParticipantId) {
        //TODO: Trade 도메인 PR이 머지되면 실제 로직으로 교체 예정
        //(임시) 프론트엔드가 먼저 UI 작업을 할 수 있도록 가짜 JSON 형태 리턴
        String dummyJason = """
            {
                "totalValuation": 10500000,
                "totalProfitLoss": 500000,
                "holdings": [
                    {"stockName": "삼성전자", "quantity": 50, "averagePrice": 75000, "currentPrice": 80000 },
                    { "stockName": "카카오", "quantity": 20, "averagePrice": 45000, "currentPrice": 42000 }
                ]
            }
            """;
        return ResponseEntity.ok().body(dummyJason);
    }
}
