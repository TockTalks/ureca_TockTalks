package com.tocktalks.domain.trade.controller;

import com.tocktalks.domain.trade.dto.response.TradeHistoryResponse;
import com.tocktalks.domain.trade.entity.TradeType;
import com.tocktalks.domain.trade.service.TradeHistoryService;
import com.tocktalks.global.exception.GlobalExceptionHandler;
import com.tocktalks.domain.trade.dto.response.HoldingResponse;
import com.tocktalks.domain.trade.dto.response.HoldingSummaryResponse;
import com.tocktalks.domain.trade.service.HoldingQueryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication
        .UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request
        .MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result
        .MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result
        .MockMvcResultMatchers.status;

class TradeControllerTest {

    private TradeHistoryService tradeHistoryService;
    private HoldingQueryService holdingQueryService;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        tradeHistoryService =
                mock(TradeHistoryService.class);

        holdingQueryService =
                mock(HoldingQueryService.class);

        TradeController tradeController =
                new TradeController(
                        tradeHistoryService,
                        holdingQueryService
                );

        mockMvc = MockMvcBuilders
                .standaloneSetup(tradeController)
                .setCustomArgumentResolvers(
                        new PageableHandlerMethodArgumentResolver()
                )
                .setControllerAdvice(
                        new GlobalExceptionHandler()
                )
                .build();
    }

    @Test
    void 로그인_회원의_거래_내역을_조회한다()
            throws Exception {
        Long memberId = 10L;
        Long roomParticipantId = 20L;

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        memberId,
                        null,
                        List.of()
                );

        TradeHistoryResponse response =
                new TradeHistoryResponse(
                        30L,
                        "005930",
                        "삼성전자",
                        TradeType.SELL,
                        2L,
                        new BigDecimal("75000.00"),
                        new BigDecimal("10000.00"),
                        new BigDecimal("7.1429"),
                        LocalDateTime.of(
                                2026,
                                7,
                                20,
                                12,
                                0
                        )
                );

        Page<TradeHistoryResponse> page =
                new PageImpl<>(
                        List.of(response),
                        PageRequest.of(0, 20),
                        1
                );

        when(tradeHistoryService.getTradeHistory(
                eq(memberId),
                eq(roomParticipantId),
                any(Pageable.class)
        )).thenReturn(page);

        mockMvc.perform(
                        get("/api/trades")
                                .principal(authentication)
                                .param(
                                        "roomParticipantId",
                                        roomParticipantId.toString()
                                )
                                .param("page", "0")
                                .param("size", "20")
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.content[0].transactionId")
                                .value(30L)
                )
                .andExpect(
                        jsonPath("$.content[0].stockCode")
                                .value("005930")
                )
                .andExpect(
                        jsonPath("$.content[0].stockName")
                                .value("삼성전자")
                )
                .andExpect(
                        jsonPath("$.content[0].type")
                                .value("SELL")
                )
                .andExpect(
                        jsonPath("$.content[0].quantity")
                                .value(2L)
                )
                .andExpect(
                        jsonPath("$.content[0].price")
                                .value(75000.00)
                )
                .andExpect(
                        jsonPath("$.content[0].profitAmount")
                                .value(10000.00)
                )
                .andExpect(
                        jsonPath("$.content[0].profitRate")
                                .value(7.1429)
                );

        verify(tradeHistoryService).getTradeHistory(
                eq(memberId),
                eq(roomParticipantId),
                any(Pageable.class)
        );
    }

    @Test
    void 로그인_회원의_보유_종목을_조회한다()
            throws Exception {
        Long memberId = 10L;
        Long roomParticipantId = 20L;

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        memberId,
                        null,
                        List.of()
                );

        HoldingResponse response =
                new HoldingResponse(
                        30L,
                        roomParticipantId,
                        "005930",
                        "삼성전자",
                        10L,
                        new BigDecimal("70000.00"),
                        new BigDecimal("75000.00"),
                        new BigDecimal("750000.00"),
                        new BigDecimal("50000.00"),
                        new BigDecimal("7.1429"),
                        LocalDateTime.of(
                                2026,
                                7,
                                20,
                                12,
                                0
                        )
                );

        when(holdingQueryService.getHoldings(
                memberId,
                roomParticipantId
        )).thenReturn(List.of(response));

        mockMvc.perform(
                        get("/api/trades/holdings")
                                .principal(authentication)
                                .param(
                                        "roomParticipantId",
                                        roomParticipantId.toString()
                                )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$[0].holdingId")
                                .value(30L)
                )
                .andExpect(
                        jsonPath("$[0].roomParticipantId")
                                .value(roomParticipantId)
                )
                .andExpect(
                        jsonPath("$[0].stockCode")
                                .value("005930")
                )
                .andExpect(
                        jsonPath("$[0].stockName")
                                .value("삼성전자")
                )
                .andExpect(
                        jsonPath("$[0].quantity")
                                .value(10L)
                )
                .andExpect(
                        jsonPath("$[0].avgPrice")
                                .value(70000.00)
                )
                .andExpect(
                        jsonPath("$[0].currentPrice")
                                .value(75000.00)
                )
                .andExpect(
                        jsonPath("$[0].valuationAmount")
                                .value(750000.00)
                )
                .andExpect(
                        jsonPath("$[0].profitLoss")
                                .value(50000.00)
                )
                .andExpect(
                        jsonPath("$[0].profitRate")
                                .value(7.1429)
                );

        verify(holdingQueryService).getHoldings(
                memberId,
                roomParticipantId
        );
    }

    @Test
    void 로그인_회원의_보유_종목_합계를_조회한다()
            throws Exception {
        Long memberId = 10L;
        Long roomParticipantId = 20L;

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        memberId,
                        null,
                        List.of()
                );

        HoldingResponse holding =
                new HoldingResponse(
                        30L,
                        roomParticipantId,
                        "005930",
                        "삼성전자",
                        10L,
                        new BigDecimal("70000.00"),
                        new BigDecimal("75000.00"),
                        new BigDecimal("750000.00"),
                        new BigDecimal("50000.00"),
                        new BigDecimal("7.1429"),
                        LocalDateTime.of(
                                2026,
                                7,
                                20,
                                12,
                                0
                        )
                );

        HoldingSummaryResponse summary =
                new HoldingSummaryResponse(
                        new BigDecimal("750000.00"),
                        new BigDecimal("50000.00"),
                        List.of(holding)
                );

        when(holdingQueryService.getHoldingSummary(
                memberId,
                roomParticipantId
        )).thenReturn(summary);

        mockMvc.perform(
                        get("/api/trades/holdings/summary")
                                .principal(authentication)
                                .param(
                                        "roomParticipantId",
                                        roomParticipantId.toString()
                                )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.totalValuation")
                                .value(750000.00)
                )
                .andExpect(
                        jsonPath("$.totalProfitLoss")
                                .value(50000.00)
                )
                .andExpect(
                        jsonPath("$.holdings[0].stockCode")
                                .value("005930")
                )
                .andExpect(
                        jsonPath("$.holdings[0].stockName")
                                .value("삼성전자")
                )
                .andExpect(
                        jsonPath("$.holdings[0].currentPrice")
                                .value(75000.00)
                )
                .andExpect(
                        jsonPath("$.holdings[0].valuationAmount")
                                .value(750000.00)
                )
                .andExpect(
                        jsonPath("$.holdings[0].profitLoss")
                                .value(50000.00)
                );

        verify(holdingQueryService).getHoldingSummary(
                memberId,
                roomParticipantId
        );
    }

    @Test
    void 인증되지_않은_사용자는_거래_내역을_조회할_수_없다()
            throws Exception {
        mockMvc.perform(
                        get("/api/trades")
                                .param(
                                        "roomParticipantId",
                                        "20"
                                )
                )
                .andExpect(status().isBadRequest())
                .andExpect(
                        jsonPath("$.message")
                                .value("인증된 사용자가 아닙니다.")
                );

        verifyNoInteractions(
                tradeHistoryService,
                holdingQueryService
        );
    }
}