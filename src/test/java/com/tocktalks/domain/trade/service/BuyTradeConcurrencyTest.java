package com.tocktalks.domain.trade.service;

import com.tocktalks.domain.room.entity.Room;
import com.tocktalks.domain.room.entity.RoomParticipant;
import com.tocktalks.domain.room.repository.RoomParticipantRepository;
import com.tocktalks.domain.room.repository.RoomRepository;
import com.tocktalks.domain.trade.dto.request.TradeOrderRequest;
import com.tocktalks.domain.trade.entity.Holding;
import com.tocktalks.domain.trade.entity.TradeType;
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
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

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
class BuyTradeConcurrencyTest {

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

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void 동시에_잔액보다_많이_매수해도_한_건만_체결된다()
            throws Exception {
        Room room = roomRepository.saveAndFlush(
                Room.createDefault(100_000L)
        );

        RoomParticipant participant =
                roomParticipantRepository.saveAndFlush(
                        RoomParticipant.join(
                                room.getId(),
                                10L,
                                100_000L
                        )
                );

        Long participantId = participant.getId();

        when(currentPriceProvider
                .getCurrentPrice("005930"))
                .thenReturn(
                        new BigDecimal("60000")
                );

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

        Callable<Boolean> orderTask = () -> {
            readySignal.countDown();
            startSignal.await();

            try {
                buyTradeService.buy(
                        10L,
                        participantId,
                        request
                );

                return true;
            } catch (IllegalArgumentException exception) {
                return false;
            }
        };

        try {
            Future<Boolean> firstOrder =
                    executor.submit(orderTask);

            Future<Boolean> secondOrder =
                    executor.submit(orderTask);

            assertThat(
                    readySignal.await(
                            5,
                            TimeUnit.SECONDS
                    )
            ).isTrue();

            startSignal.countDown();

            Boolean firstResult =
                    firstOrder.get(
                            10,
                            TimeUnit.SECONDS
                    );

            Boolean secondResult =
                    secondOrder.get(
                            10,
                            TimeUnit.SECONDS
                    );

            assertThat(
                    List.of(
                            firstResult,
                            secondResult
                    )
            ).containsExactlyInAnyOrder(
                    true,
                    false
            );
        } finally {
            executor.shutdownNow();
        }

        RoomParticipant savedParticipant =
                roomParticipantRepository
                        .findById(participantId)
                        .orElseThrow();

        Holding holding = holdingRepository
                .findByRoomParticipantIdAndStockCode(
                        participantId,
                        "005930"
                )
                .orElseThrow();

        List<Transaction> transactions =
                transactionRepository.findAll()
                        .stream()
                        .filter(transaction ->
                                transaction
                                        .getRoomParticipantId()
                                        .equals(participantId)
                        )
                        .toList();

        assertThat(savedParticipant.getBalance())
                .isEqualTo(40_000L);

        assertThat(holding.getQuantity())
                .isEqualTo(1L);

        assertThat(transactions).hasSize(1);
        assertThat(transactions.get(0).getType())
                .isEqualTo(TradeType.BUY);
    }
}