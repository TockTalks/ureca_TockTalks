package com.tocktalks.domain.trade.service;

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
        SellTradeService.class,
        SellTradeProcessor.class
})
class SellTradeRollbackIntegrationTest {

    @Autowired
    private SellTradeService sellTradeService;

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

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void 잔액_입금에_실패하면_보유_종목과_거래_내역이_모두_롤백된다() {
        Room room = roomRepository.saveAndFlush(
                Room.createDefault(Long.MAX_VALUE)
        );

        RoomParticipant participant =
                roomParticipantRepository.saveAndFlush(
                        RoomParticipant.join(
                                room.getId(),
                                10L,
                                Long.MAX_VALUE
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

        TradeOrderRequest request =
                new TradeOrderRequest(
                        "005930",
                        1L
                );

        assertThatThrownBy(() ->
                sellTradeService.sell(
                        10L,
                        participantId,
                        request
                )
        )
                .isInstanceOf(
                        IllegalArgumentException.class
                )
                .hasMessage(
                        "거래 잔액이 허용 범위를 초과합니다."
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
                .isEqualTo(Long.MAX_VALUE);

        assertThat(savedHolding.getQuantity())
                .isEqualTo(1L);
        assertThat(savedHolding.getAvgPrice())
                .isEqualByComparingTo("50000.00");

        assertThat(transactionCount).isZero();
    }
}