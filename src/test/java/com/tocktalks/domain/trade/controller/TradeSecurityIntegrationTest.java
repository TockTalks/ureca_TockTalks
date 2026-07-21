package com.tocktalks.domain.trade.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@SpringBootTest
@WebAppConfiguration
class TradeSecurityIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    void 미인증_사용자는_거래_내역을_조회할_수_없다()
            throws Exception {
        mockMvc.perform(
                        get("/api/trades")
                                .param(
                                        "roomParticipantId",
                                        "1"
                                )
                )
                .andExpect(
                        status().isUnauthorized()
                );
    }

    @Test
    void 미인증_사용자는_종목을_매수할_수_없다()
            throws Exception {
        mockMvc.perform(
                        post("/api/trades/buy")
                                .param(
                                        "roomParticipantId",
                                        "1"
                                )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content("""
                                        {
                                          "stockCode": "005930",
                                          "quantity": 1
                                        }
                                        """)
                )
                .andExpect(
                        status().isUnauthorized()
                );
    }

    @Test
    void 미인증_사용자는_종목을_매도할_수_없다()
            throws Exception {
        mockMvc.perform(
                        post("/api/trades/sell")
                                .param(
                                        "roomParticipantId",
                                        "1"
                                )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content("""
                                        {
                                          "stockCode": "005930",
                                          "quantity": 1
                                        }
                                        """)
                )
                .andExpect(
                        status().isUnauthorized()
                );
    }
}