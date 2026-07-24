package com.tocktalks.domain.trade.service;

import com.tocktalks.domain.portfolio.service.PortfolioService;
import com.tocktalks.domain.room.entity.Room;
import com.tocktalks.domain.room.entity.RoomParticipant;
import com.tocktalks.domain.room.repository.RoomParticipantRepository;
import com.tocktalks.domain.room.repository.RoomRepository;
import com.tocktalks.domain.trade.dto.request.TradeOrderRequest;
import com.tocktalks.domain.trade.entity.Holding;
import com.tocktalks.domain.trade.entity.Transaction;
import com.tocktalks.domain.trade.repository.HoldingRepository;
import com.tocktalks.domain.trade.repository.TransactionRepository;
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
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
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
class DifferentParticipantConcurrencyTest {

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

    // 거래 후 포트폴리오 이력 기록은 동시성 검증의 대상이 아니므로 격리한다.
    @MockitoBean
    private PortfolioService portfolioService;

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void 다른_참가자의_주문은_서로를_잠그지_않고_동시에_처리된다()
            throws Exception {
        Room room = roomRepository.saveAndFlush(
                TradeTestRoomFactory.ongoing(100_000L)
        );

        RoomParticipant firstParticipant =
                roomParticipantRepository.saveAndFlush(
                        RoomParticipant.join(
                                room.getId(),
                                10L,
                                100_000L
                        )
                );

        RoomParticipant secondParticipant =
                roomParticipantRepository.saveAndFlush(
                        RoomParticipant.join(
                                room.getId(),
                                11L,
                                100_000L
                        )
                );

        CountDownLatch priceRequestSignal =
                new CountDownLatch(2);

        when(currentPriceProvider
                .getCurrentPrice("005930"))
                .thenAnswer(invocation -> {
                    priceRequestSignal.countDown();

                    boolean bothReached =
                            priceRequestSignal.await(
                                    5,
                                    TimeUnit.SECONDS
                            );

                    if (!bothReached) {
                        throw new IllegalStateException(
                                "다른 참가자의 주문이 불필요하게 잠겼습니다."
                        );
                    }

                    return new BigDecimal("60000");
                });

        when(stockNameProvider
                .getStockName("005930"))
                .thenReturn("삼성전자");

        TradeOrderRequest request =
                new TradeOrderRequest(
                        "005930",
                        1L
                );

        ExecutorService executor =
                Executors.newFixedThreadPool(2);

        CountDownLatch readySignal =
                new CountDownLatch(2);

        CountDownLatch startSignal =
                new CountDownLatch(1);

        Callable<Boolean> firstOrder = () -> {
            readySignal.countDown();
            startSignal.await();

            buyTradeService.buy(
                    10L,
                    firstParticipant.getId(),
                    request
            );

            return true;
        };

        Callable<Boolean> secondOrder = () -> {
            readySignal.countDown();
            startSignal.await();

            buyTradeService.buy(
                    11L,
                    secondParticipant.getId(),
                    request
            );

            return true;
        };

        try {
            Future<Boolean> firstResult =
                    executor.submit(firstOrder);

            Future<Boolean> secondResult =
                    executor.submit(secondOrder);

            assertThat(
                    readySignal.await(
                            5,
                            TimeUnit.SECONDS
                    )
            ).isTrue();

            startSignal.countDown();

            assertThat(
                    firstResult.get(
                            10,
                            TimeUnit.SECONDS
                    )
            ).isTrue();

            assertThat(
                    secondResult.get(
                            10,
                            TimeUnit.SECONDS
                    )
            ).isTrue();
        } finally {
            executor.shutdownNow();
        }

        RoomParticipant savedFirstParticipant =
                roomParticipantRepository
                        .findById(
                                firstParticipant.getId()
                        )
                        .orElseThrow();

        RoomParticipant savedSecondParticipant =
                roomParticipantRepository
                        .findById(
                                secondParticipant.getId()
                        )
                        .orElseThrow();

        Holding firstHolding = holdingRepository
                .findByRoomParticipantIdAndStockCode(
                        firstParticipant.getId(),
                        "005930"
                )
                .orElseThrow();

        Holding secondHolding = holdingRepository
                .findByRoomParticipantIdAndStockCode(
                        secondParticipant.getId(),
                        "005930"
                )
                .orElseThrow();

        assertThat(savedFirstParticipant.getBalance())
                .isEqualTo(40_000L);
        assertThat(savedSecondParticipant.getBalance())
                .isEqualTo(40_000L);

        assertThat(firstHolding.getQuantity())
                .isEqualTo(1L);
        assertThat(secondHolding.getQuantity())
                .isEqualTo(1L);

        List<Transaction> transactions =
                transactionRepository.findAll()
                        .stream()
                        .filter(transaction ->
                                transaction
                                        .getRoomParticipantId()
                                        .equals(
                                                firstParticipant.getId()
                                        )
                                        || transaction
                                        .getRoomParticipantId()
                                        .equals(
                                                secondParticipant.getId()
                                        )
                        )
                        .toList();

        assertThat(transactions).hasSize(2);
    }
}
