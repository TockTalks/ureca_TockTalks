package com.tocktalks.domain.trade.service;

import com.tocktalks.domain.ranking.service.RankingService;
import com.tocktalks.domain.room.entity.Room;
import com.tocktalks.domain.room.entity.RoomParticipant;
import com.tocktalks.domain.room.repository.RoomParticipantRepository;
import com.tocktalks.domain.room.repository.RoomRepository;
import com.tocktalks.domain.trade.dto.request.TradeOrderRequest;
import com.tocktalks.domain.trade.entity.Holding;
import com.tocktalks.domain.trade.repository.HoldingRepository;
import com.tocktalks.support.MySqlTestContainerConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DataJpaTest(
        properties =
                "spring.jpa.hibernate.ddl-auto=create-drop"
)
@AutoConfigureTestDatabase(
        replace = AutoConfigureTestDatabase.Replace.NONE
)
@Import({
        MySqlTestContainerConfiguration.class,
        BuyTradeService.class,
        BuyTradeProcessor.class,
        SellTradeService.class,
        SellTradeProcessor.class,
        TradeAssetService.class,
        TradeRankingService.class
})
class TradeRankingIntegrationTest {

    @Autowired
    private BuyTradeService buyTradeService;

    @Autowired
    private SellTradeService sellTradeService;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private RoomParticipantRepository
            roomParticipantRepository;

    @Autowired
    private HoldingRepository holdingRepository;

    @MockitoBean
    private CurrentPriceProvider currentPriceProvider;

    @MockitoBean
    private StockNameProvider stockNameProvider;

    @MockitoBean
    private RankingService rankingService;

    @Test
    @Transactional(
            propagation = Propagation.NOT_SUPPORTED
    )
    void buyUpdatesRankingWithTotalAsset() {
        Room room = roomRepository.saveAndFlush(
                Room.createDefault(1_000_000L)
        );

        RoomParticipant participant =
                roomParticipantRepository.saveAndFlush(
                        RoomParticipant.join(
                                room.getId(),
                                10L,
                                1_000_000L
                        )
                );

        when(currentPriceProvider.getCurrentPrice(
                "005930"
        )).thenReturn(new BigDecimal("75000"));

        when(stockNameProvider.getStockName(
                "005930"
        )).thenReturn("삼성전자");

        buyTradeService.buy(
                10L,
                participant.getId(),
                new TradeOrderRequest(
                        "005930",
                        2L
                )
        );

        verify(rankingService).updateRanking(
                room.getId(),
                10L,
                1_000_000L,
                1_000_000L
        );
    }

    @Test
    @Transactional(
            propagation = Propagation.NOT_SUPPORTED
    )
    void sellUpdatesRankingWithTotalAsset() {
        Room room = roomRepository.saveAndFlush(
                Room.createDefault(1_000_000L)
        );

        RoomParticipant participant =
                roomParticipantRepository.saveAndFlush(
                        RoomParticipant.join(
                                room.getId(),
                                20L,
                                1_000_000L
                        )
                );

        holdingRepository.saveAndFlush(
                Holding.create(
                        participant.getId(),
                        "005930",
                        "삼성전자",
                        3L,
                        new BigDecimal("70000")
                )
        );

        when(currentPriceProvider.getCurrentPrice(
                "005930"
        )).thenReturn(new BigDecimal("75000"));

        sellTradeService.sell(
                20L,
                participant.getId(),
                new TradeOrderRequest(
                        "005930",
                        1L
                )
        );

        verify(rankingService).updateRanking(
                room.getId(),
                20L,
                1_225_000L,
                1_000_000L
        );
    }
}