package com.tocktalks.domain.room.repository;

import com.tocktalks.domain.room.entity.RoomParticipant;
import com.tocktalks.support.MySqlTestContainerConfiguration;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = "spring.jpa.hibernate.ddl-auto=create-drop")
@AutoConfigureTestDatabase(
        replace = AutoConfigureTestDatabase.Replace.NONE
)
@Import(MySqlTestContainerConfiguration.class)
class RoomParticipantRepositoryTest {

    @Autowired
    private RoomParticipantRepository
            roomParticipantRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void 활성_참가자를_소유권과_함께_쓰기_잠금으로_조회한다() {
        RoomParticipant participant =
                roomParticipantRepository.saveAndFlush(
                        RoomParticipant.join(
                                1L,
                                10L,
                                1_000_000L
                        )
                );

        Long participantId = participant.getId();

        entityManager.clear();

        Optional<RoomParticipant> found =
                roomParticipantRepository
                        .findActiveForUpdate(
                                participantId,
                                10L
                        );

        assertThat(found).isPresent();
        assertThat(found.get().getId())
                .isEqualTo(participantId);
        assertThat(found.get().getMemberId())
                .isEqualTo(10L);
        assertThat(found.get().getStatus())
                .isEqualTo("ACTIVE");
        assertThat(entityManager.getLockMode(found.get()))
                .isEqualTo(
                        LockModeType.PESSIMISTIC_WRITE
                );
    }

    @Test
    void 다른_회원의_참가자는_잠금_조회할_수_없다() {
        RoomParticipant participant =
                roomParticipantRepository.saveAndFlush(
                        RoomParticipant.join(
                                1L,
                                10L,
                                1_000_000L
                        )
                );

        Long participantId = participant.getId();

        entityManager.clear();

        Optional<RoomParticipant> found =
                roomParticipantRepository
                        .findActiveForUpdate(
                                participantId,
                                11L
                        );

        assertThat(found).isEmpty();
    }

    @Test
    void 종료된_참가자는_잠금_조회할_수_없다() {
        RoomParticipant participant =
                RoomParticipant.join(
                        1L,
                        10L,
                        1_000_000L
                );

        participant.end();

        RoomParticipant savedParticipant =
                roomParticipantRepository
                        .saveAndFlush(participant);

        Long participantId =
                savedParticipant.getId();

        entityManager.clear();

        Optional<RoomParticipant> found =
                roomParticipantRepository
                        .findActiveForUpdate(
                                participantId,
                                10L
                        );

        assertThat(found).isEmpty();
    }
}