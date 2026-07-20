package com.tocktalks.domain.trade.repository;

import com.tocktalks.domain.trade.entity.TradeType;
import com.tocktalks.domain.trade.entity.Transaction;
import com.tocktalks.support.MySqlTestContainerConfiguration;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = "spring.jpa.hibernate.ddl-auto=create-drop")
@AutoConfigureTestDatabase(
        replace = AutoConfigureTestDatabase.Replace.NONE
)
@Import(MySqlTestContainerConfiguration.class)
class TransactionRepositoryTest {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void 거래_내역을_저장하고_조회한다() {
        Transaction transaction = Transaction.createBuy(
                1L,
                "005930",
                "삼성전자",
                10L,
                new BigDecimal("70000")
        );

        Transaction savedTransaction =
                transactionRepository.saveAndFlush(transaction);

        Long transactionId = savedTransaction.getId();

        entityManager.clear();

        Optional<Transaction> found =
                transactionRepository.findById(transactionId);

        assertThat(found).isPresent();
        assertThat(found.get().getRoomParticipantId())
                .isEqualTo(1L);
        assertThat(found.get().getStockCode())
                .isEqualTo("005930");
        assertThat(found.get().getType())
                .isEqualTo(TradeType.BUY);
        assertThat(found.get().getQuantity())
                .isEqualTo(10L);
        assertThat(found.get().getPrice())
                .isEqualByComparingTo("70000.00");
        assertThat(found.get().getProfitAmount())
                .isNull();
        assertThat(found.get().getProfitRate())
                .isNull();
        assertThat(found.get().getExecutedAt())
                .isNotNull();
    }

    @Test
    void 참가자의_거래_내역을_최신순으로_조회한다()
            throws InterruptedException {

        Transaction olderBuy = Transaction.createBuy(
                1L,
                "005930",
                "삼성전자",
                10L,
                new BigDecimal("70000")
        );

        Thread.sleep(10);

        Transaction newerSell = Transaction.createSell(
                1L,
                "000660",
                "SK하이닉스",
                2L,
                new BigDecimal("190000"),
                new BigDecimal("180000")
        );

        Transaction otherParticipant = Transaction.createBuy(
                2L,
                "035420",
                "NAVER",
                3L,
                new BigDecimal("200000")
        );

        transactionRepository.saveAll(
                List.of(
                        olderBuy,
                        newerSell,
                        otherParticipant
                )
        );
        transactionRepository.flush();
        entityManager.clear();

        Page<Transaction> result =
                transactionRepository
                        .findAllByRoomParticipantIdOrderByExecutedAtDesc(
                                1L,
                                PageRequest.of(0, 10)
                        );

        assertThat(result.getTotalElements())
                .isEqualTo(2);

        assertThat(result.getContent())
                .extracting(Transaction::getRoomParticipantId)
                .containsOnly(1L);

        assertThat(result.getContent())
                .extracting(Transaction::getType)
                .containsExactly(
                        TradeType.SELL,
                        TradeType.BUY
                );

        assertThat(result.getContent())
                .extracting(Transaction::getStockCode)
                .containsExactly(
                        "000660",
                        "005930"
                );
    }

    @Test
    void 참가자의_거래_내역을_페이지로_나누어_조회한다()
            throws InterruptedException {

        Transaction oldest = Transaction.createBuy(
                1L,
                "005930",
                "삼성전자",
                10L,
                new BigDecimal("70000")
        );

        Thread.sleep(10);

        Transaction middle = Transaction.createBuy(
                1L,
                "000660",
                "SK하이닉스",
                5L,
                new BigDecimal("180000")
        );

        Thread.sleep(10);

        Transaction newest = Transaction.createBuy(
                1L,
                "035420",
                "NAVER",
                3L,
                new BigDecimal("200000")
        );

        transactionRepository.saveAll(
                List.of(oldest, middle, newest)
        );
        transactionRepository.flush();
        entityManager.clear();

        Page<Transaction> firstPage =
                transactionRepository
                        .findAllByRoomParticipantIdOrderByExecutedAtDesc(
                                1L,
                                PageRequest.of(0, 2)
                        );

        Page<Transaction> secondPage =
                transactionRepository
                        .findAllByRoomParticipantIdOrderByExecutedAtDesc(
                                1L,
                                PageRequest.of(1, 2)
                        );

        assertThat(firstPage.getTotalElements())
                .isEqualTo(3);
        assertThat(firstPage.getTotalPages())
                .isEqualTo(2);
        assertThat(firstPage.getNumber())
                .isZero();
        assertThat(firstPage.hasNext())
                .isTrue();

        assertThat(firstPage.getContent())
                .extracting(Transaction::getStockCode)
                .containsExactly(
                        "035420",
                        "000660"
                );

        assertThat(secondPage.getNumber())
                .isEqualTo(1);
        assertThat(secondPage.hasNext())
                .isFalse();

        assertThat(secondPage.getContent())
                .extracting(Transaction::getStockCode)
                .containsExactly("005930");
    }

    @Test
    void 회원이_소유한_거래만_조회한다() {
        Long roomParticipantId = 100L;
        Long memberId = 200L;

        entityManager.createNativeQuery("""
                    INSERT INTO room_participant (
                        id,
                        room_id,
                        member_id,
                        balance,
                        initial_seed_money,
                        joined_at
                    ) VALUES (
                        :id,
                        :roomId,
                        :memberId,
                        :balance,
                        :initialSeedMoney,
                        :joinedAt
                    )
                    """)
                .setParameter("id", roomParticipantId)
                .setParameter("roomId", 1L)
                .setParameter("memberId", memberId)
                .setParameter("balance", 1_000_000L)
                .setParameter("initialSeedMoney", 1_000_000L)
                .setParameter(
                        "joinedAt",
                        java.time.LocalDateTime.now()
                )
                .executeUpdate();

        Transaction transaction = Transaction.createSell(
                roomParticipantId,
                "005930",
                "삼성전자",
                2L,
                new BigDecimal("75000"),
                new BigDecimal("70000")
        );

        Transaction savedTransaction =
                transactionRepository.saveAndFlush(transaction);

        Long transactionId = savedTransaction.getId();

        entityManager.clear();

        Optional<Transaction> ownedTransaction =
                transactionRepository.findOwnedByIdAndMemberId(
                        transactionId,
                        memberId
                );

        Optional<Transaction> otherMemberTransaction =
                transactionRepository.findOwnedByIdAndMemberId(
                        transactionId,
                        201L
                );

        assertThat(ownedTransaction).isPresent();
        assertThat(ownedTransaction.get().getId())
                .isEqualTo(transactionId);
        assertThat(ownedTransaction.get().getType())
                .isEqualTo(TradeType.SELL);
        assertThat(otherMemberTransaction).isEmpty();
    }
}