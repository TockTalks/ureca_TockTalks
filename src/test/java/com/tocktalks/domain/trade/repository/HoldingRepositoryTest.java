package com.tocktalks.domain.trade.repository;

import com.tocktalks.domain.trade.entity.Holding;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.dao.DataIntegrityViolationException;
import com.tocktalks.support.MySqlTestContainerConfiguration;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest(properties = "spring.jpa.hibernate.ddl-auto=create-drop")
@AutoConfigureTestDatabase(
        replace = AutoConfigureTestDatabase.Replace.NONE
)
@Import(MySqlTestContainerConfiguration.class)
class HoldingRepositoryTest {

    @Autowired
    private HoldingRepository holdingRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void 보유_종목을_저장하고_참가자와_종목코드로_조회한다() {
        Holding holding = Holding.create(
                1L,
                "005930",
                "삼성전자",
                10L,
                new BigDecimal("70000")
        );

        Holding savedHolding =
                holdingRepository.saveAndFlush(holding);

        entityManager.clear();

        Optional<Holding> found =
                holdingRepository
                        .findByRoomParticipantIdAndStockCode(
                                1L,
                                "005930"
                        );

        assertThat(found).isPresent();
        assertThat(found.get().getId())
                .isEqualTo(savedHolding.getId());
        assertThat(found.get().getStockName())
                .isEqualTo("삼성전자");
        assertThat(found.get().getQuantity())
                .isEqualTo(10L);
        assertThat(found.get().getAvgPrice())
                .isEqualByComparingTo("70000.00");
    }

    @Test
    void 참가자의_전체_보유_종목을_조회한다() {
        holdingRepository.save(
                Holding.create(
                        1L,
                        "005930",
                        "삼성전자",
                        10L,
                        new BigDecimal("70000")
                )
        );

        holdingRepository.save(
                Holding.create(
                        1L,
                        "000660",
                        "SK하이닉스",
                        5L,
                        new BigDecimal("180000")
                )
        );

        holdingRepository.save(
                Holding.create(
                        2L,
                        "035420",
                        "NAVER",
                        3L,
                        new BigDecimal("200000")
                )
        );

        holdingRepository.flush();
        entityManager.clear();

        List<Holding> holdings =
                holdingRepository.findAllByRoomParticipantId(1L);

        assertThat(holdings)
                .hasSize(2)
                .extracting(Holding::getStockCode)
                .containsExactlyInAnyOrder(
                        "005930",
                        "000660"
                );
    }

    @Test
    void 같은_참가자와_종목코드는_중복_저장할_수_없다() {
        holdingRepository.saveAndFlush(
                Holding.create(
                        1L,
                        "005930",
                        "삼성전자",
                        10L,
                        new BigDecimal("70000")
                )
        );

        Holding duplicate = Holding.create(
                1L,
                "005930",
                "삼성전자",
                5L,
                new BigDecimal("71000")
        );

        assertThatThrownBy(
                () -> holdingRepository.saveAndFlush(duplicate)
        ).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void 보유_종목을_쓰기_잠금으로_조회한다() {
        Holding holding = Holding.create(
                1L,
                "005930",
                "삼성전자",
                10L,
                new BigDecimal("70000")
        );

        Holding savedHolding =
                holdingRepository.saveAndFlush(holding);

        entityManager.clear();

        Optional<Holding> found =
                holdingRepository.findForUpdate(
                        1L,
                        "005930"
                );

        assertThat(found).isPresent();
        assertThat(found.get().getId())
                .isEqualTo(savedHolding.getId());
        assertThat(entityManager.getLockMode(found.get()))
                .isEqualTo(LockModeType.PESSIMISTIC_WRITE);
    }
}