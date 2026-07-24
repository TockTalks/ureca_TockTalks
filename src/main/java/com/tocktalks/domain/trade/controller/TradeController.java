package com.tocktalks.domain.trade.controller;

import com.tocktalks.domain.trade.dto.response.HoldingResponse;
import com.tocktalks.domain.trade.dto.response.TradeHistoryResponse;
import com.tocktalks.domain.trade.dto.response.HoldingSummaryResponse;
import com.tocktalks.domain.trade.dto.request.TradeOrderRequest;
import com.tocktalks.domain.trade.dto.response.TradeExecutionResponse;
import com.tocktalks.domain.trade.service.BuyTradeService;
import com.tocktalks.domain.trade.service.SellTradeService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.tocktalks.domain.trade.service.HoldingQueryService;
import com.tocktalks.domain.trade.service.TradeHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/trades")
@RequiredArgsConstructor
public class TradeController {

    private final TradeHistoryService tradeHistoryService;
    private final HoldingQueryService holdingQueryService;
    private final BuyTradeService buyTradeService;
    private final SellTradeService sellTradeService;

    @GetMapping
    public ResponseEntity<Page<TradeHistoryResponse>>
    getTradeHistory(
            Authentication authentication,
            @RequestParam Long roomParticipantId,
            @PageableDefault(size = 20)
            Pageable pageable
    ) {
        Long memberId = extractMemberId(authentication);

        return ResponseEntity.ok(
                tradeHistoryService.getTradeHistory(
                        memberId,
                        roomParticipantId,
                        pageable
                )
        );
    }

    @GetMapping("/holdings")
    public ResponseEntity<List<HoldingResponse>>
    getHoldings(
            Authentication authentication,
            @RequestParam Long roomParticipantId
    ) {
        Long memberId = extractMemberId(authentication);

        return ResponseEntity.ok(
                holdingQueryService.getHoldings(
                        memberId,
                        roomParticipantId
                )
        );
    }

    @GetMapping("/holdings/summary")
    public ResponseEntity<HoldingSummaryResponse>
    getHoldingSummary(
            Authentication authentication,
            @RequestParam Long roomParticipantId
    ) {
        Long memberId = extractMemberId(authentication);

        return ResponseEntity.ok(
                holdingQueryService.getHoldingSummary(
                        memberId,
                        roomParticipantId
                )
        );
    }

    @PostMapping("/buy")
    public ResponseEntity<TradeExecutionResponse>
    buy(
            Authentication authentication,
            @RequestParam Long roomParticipantId,
            @Valid @RequestBody
            TradeOrderRequest request
    ) {
        Long memberId =
                extractMemberId(authentication);

        return ResponseEntity.ok(
                buyTradeService.buy(
                        memberId,
                        roomParticipantId,
                        request
                )
        );
    }

    @PostMapping("/sell")
    public ResponseEntity<TradeExecutionResponse>
    sell(
            Authentication authentication,
            @RequestParam Long roomParticipantId,
            @Valid @RequestBody
            TradeOrderRequest request
    ) {
        Long memberId =
                extractMemberId(authentication);

        return ResponseEntity.ok(
                sellTradeService.sell(
                        memberId,
                        roomParticipantId,
                        request
                )
        );
    }

    private Long extractMemberId(
            Authentication authentication
    ) {
        if (authentication == null
                || !(authentication.getPrincipal()
                instanceof Long memberId)) {
            throw new IllegalArgumentException(
                    "인증된 사용자가 아닙니다."
            );
        }

        return memberId;
    }
}