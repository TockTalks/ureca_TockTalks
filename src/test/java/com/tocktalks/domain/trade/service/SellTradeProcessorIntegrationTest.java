package com.tocktalks.domain.trade.service;

import com.tocktalks.domain.trade.entity.Holding;
import com.tocktalks.domain.trade.entity.TradeType;
import com.tocktalks.domain.trade.entity.Transaction;
import com.tocktalks.domain.trade.repository.HoldingRepository;
import com.tocktalks.domain.trade.repository.TransactionRepository;
import com.tocktalks.support.MySqlTestContainerConfiguration;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = "spring.jpa.hibernate.ddl-auto=create-drop")
@AutoConfigureTestDatabase(
        replace = AutoConfigureTestDatabase.Replace.NONE
)
@Import({
        MySqlTestContainerConfiguration.class,
        SellTradeProcessor.class
})
class SellTradeProcessorIntegrationTest {

    @Autowired
    private SellTradeProcessor sellTradeProcessor;

    @Autowired
    private HoldingRepository holdingRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void 일부_매도하면_보유_수량과_거래_내역이_함께_저장된다() {
        holdingRepository.saveAndFlush(
                Holding.create(
                        1L,
                        "005930",
                        "삼성전자",
                        10L,
                        new BigDecimal("70000")
                )
        );

        Transaction transaction =
                sellTradeProcessor.process(
                        1L,
                        "005930",
                        4L,
                        new BigDecimal("75000")
                );

        entityManager.flush();
        entityManager.clear();

        Holding holding = holdingRepository
                .findByRoomParticipantIdAndStockCode(
                        1L,
                        "005930"
                )
                .orElseThrow();

        Transaction savedTransaction =
                transactionRepository
                        .findById(transaction.getId())
                        .orElseThrow();

        assertThat(holding.getQuantity())
                .isEqualTo(6L);

        assertThat(savedTransaction.getType())
                .isEqualTo(TradeType.SELL);
        assertThat(savedTransaction.getQuantity())
                .isEqualTo(4L);
        assertThat(savedTransaction.getProfitAmount())
                .isEqualByComparingTo("20000.00");
        assertThat(savedTransaction.getProfitRate())
                .isEqualByComparingTo("7.1429");
    }

    @Test
    void 전량_매도하면_보유_종목은_삭제되고_거래_내역은_저장된다() {
        holdingRepository.saveAndFlush(
                Holding.create(
                        1L,
                        "005930",
                        "삼성전자",
                        3L,
                        new BigDecimal("70000")
                )
        );

        Transaction transaction =
                sellTradeProcessor.process(
                        1L,
                        "005930",
                        3L,
                        new BigDecimal("65000")
                );

        entityManager.flush();
        entityManager.clear();

        assertThat(
                holdingRepository
                        .findByRoomParticipantIdAndStockCode(
                                1L,
                                "005930"
                        )
        ).isEmpty();

        Transaction savedTransaction =
                transactionRepository
                        .findById(transaction.getId())
                        .orElseThrow();

        assertThat(savedTransaction.getType())
                .isEqualTo(TradeType.SELL);
        assertThat(savedTransaction.getProfitAmount())
                .isEqualByComparingTo("-15000.00");
        assertThat(savedTransaction.getProfitRate())
                .isEqualByComparingTo("-7.1429");
    }
}