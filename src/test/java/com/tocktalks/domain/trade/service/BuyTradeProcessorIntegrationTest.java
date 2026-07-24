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
        BuyTradeProcessor.class
})
class BuyTradeProcessorIntegrationTest {

    @Autowired
    private BuyTradeProcessor buyTradeProcessor;

    @Autowired
    private HoldingRepository holdingRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void 매수하면_보유_종목과_거래_내역이_함께_저장된다() {
        Transaction transaction =
                buyTradeProcessor.process(
                        1L,
                        "005930",
                        "삼성전자",
                        3L,
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
                .isEqualTo(3L);
        assertThat(holding.getAvgPrice())
                .isEqualByComparingTo("75000.00");

        assertThat(savedTransaction.getType())
                .isEqualTo(TradeType.BUY);
        assertThat(savedTransaction.getStockCode())
                .isEqualTo("005930");
        assertThat(savedTransaction.getStockName())
                .isEqualTo("삼성전자");
        assertThat(savedTransaction.getQuantity())
                .isEqualTo(3L);
        assertThat(savedTransaction.getPrice())
                .isEqualByComparingTo("75000.00");
    }
}