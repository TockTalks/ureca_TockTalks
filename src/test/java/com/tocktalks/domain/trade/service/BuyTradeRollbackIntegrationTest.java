package com.tocktalks.domain.trade.service;

import com.tocktalks.domain.portfolio.service.PortfolioService;
import com.tocktalks.domain.room.entity.Room;
import com.tocktalks.domain.room.entity.RoomParticipant;
import com.tocktalks.domain.room.repository.RoomParticipantRepository;
import com.tocktalks.domain.room.repository.RoomRepository;
import com.tocktalks.domain.trade.dto.request.TradeOrderRequest;
import com.tocktalks.domain.trade.entity.Holding;
import com.tocktalks.domain.trade.repository.HoldingRepository;
import com.tocktalks.domain.trade.repository.TransactionRepository;
import com.tocktalks.support.MySqlTestContainerConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@DataJpaTest(properties = "spring.jpa.hibernate.ddl-auto=create-drop")
@AutoConfigureTestDatabase(
        replace = AutoConfigureTestDatabase.Replace.NONE
)
@Import({
        MySqlTestContainerConfiguration.class,
        BuyTradeService.class,
        BuyTradeProcessor.class
})
class BuyTradeRollbackIntegrationTest {

    @Autowired
    private BuyTradeService buyTradeService;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private RoomParticipantRepository
            roomParticipantRepository;

    @Autowired
    private HoldingRepository holdingRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @MockitoBean
    private CurrentPriceProvider currentPriceProvider;

    @MockitoBean
    private StockNameProvider stockNameProvider;

    @MockitoBean
    private TradeRankingService tradeRankingService;

    // 거래 후 포트폴리오 이력 기록은 롤백 검증의 대상이 아니므로 격리한다.
    @MockitoBean
    private PortfolioService portfolioService;

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void 거래_저장에_실패하면_잔액과_보유_종목이_모두_롤백된다() {
        Room room = roomRepository.saveAndFlush(
                TradeTestRoomFactory.ongoing(1_000_000L)
        );

        RoomParticipant participant =
                roomParticipantRepository.saveAndFlush(
                        RoomParticipant.join(
                                room.getId(),
                                10L,
                                1_000_000L
                        )
                );

        Long participantId = participant.getId();

        holdingRepository.saveAndFlush(
                Holding.create(
                        participantId,
                        "005930",
                        "삼성전자",
                        1L,
                        new BigDecimal("50000")
                )
        );

        when(currentPriceProvider
                .getCurrentPrice("005930"))
                .thenReturn(
                        new BigDecimal("60000")
                );

        when(stockNameProvider
                .getStockName("005930"))
                .thenReturn(
                        "가".repeat(101)
                );

        TradeOrderRequest request =
                new TradeOrderRequest(
                        "005930",
                        1L
                );

        assertThatThrownBy(() ->
                buyTradeService.buy(
                        10L,
                        participantId,
                        request
                )
        ).isInstanceOf(
                DataIntegrityViolationException.class
        );

        RoomParticipant savedParticipant =
                roomParticipantRepository
                        .findById(participantId)
                        .orElseThrow();

        Holding savedHolding = holdingRepository
                .findByRoomParticipantIdAndStockCode(
                        participantId,
                        "005930"
                )
                .orElseThrow();

        long transactionCount =
                transactionRepository.findAll()
                        .stream()
                        .filter(transaction ->
                                transaction
                                        .getRoomParticipantId()
                                        .equals(participantId)
                        )
                        .count();

        assertThat(savedParticipant.getBalance())
                .isEqualTo(1_000_000L);

        assertThat(savedHolding.getQuantity())
                .isEqualTo(1L);
        assertThat(savedHolding.getAvgPrice())
                .isEqualByComparingTo("50000.00");

        assertThat(transactionCount).isZero();
    }
}
