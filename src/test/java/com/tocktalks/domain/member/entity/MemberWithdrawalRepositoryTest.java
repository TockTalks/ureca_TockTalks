package com.tocktalks.domain.member.entity;

import static org.assertj.core.api.Assertions.assertThat;

import com.tocktalks.domain.member.repository.MemberRepository;
import com.tocktalks.domain.room.entity.RoomParticipant;
import com.tocktalks.domain.room.repository.RoomParticipantRepository;
import com.tocktalks.support.MySqlTestContainerConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;

/**
 * 탈퇴한 이메일·카카오 계정으로 재가입해도 과거 회원이 아니라 새 회원으로 생성되는지 검증한다.
 */
@DataJpaTest(properties = "spring.jpa.hibernate.ddl-auto=create-drop")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(MySqlTestContainerConfiguration.class)
class MemberWithdrawalRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private RoomParticipantRepository roomParticipantRepository;

    @Test
    void localRejoinCreatesNewMemberWithoutRestoringWithdrawnMember() {
        Member withdrawnMember = memberRepository.saveAndFlush(
                Member.ofLocal("rejoin@example.com", "old-password", "기존회원")
        );
        Long withdrawnMemberId = withdrawnMember.getId();
        withdrawnMember.withdraw();
        memberRepository.flush();

        Member rejoinedMember = memberRepository.saveAndFlush(
                Member.ofLocal("rejoin@example.com", "new-password", "재가입회원")
        );

        assertThat(rejoinedMember.getId()).isNotEqualTo(withdrawnMemberId);
        assertThat(memberRepository.findById(withdrawnMemberId))
                .get()
                .extracting(Member::getStatus)
                .isEqualTo("withdrawn");
        assertThat(memberRepository.findByEmail("rejoin@example.com"))
                .contains(rejoinedMember);
    }

    @Test
    void kakaoRejoinCreatesNewMemberWithoutRestoringWithdrawnMember() {
        Member withdrawnMember = memberRepository.saveAndFlush(
                Member.ofKakao("kakao@example.com", "기존회원", "same-kakao-sub")
        );
        Long withdrawnMemberId = withdrawnMember.getId();
        withdrawnMember.withdraw();
        memberRepository.flush();

        Member rejoinedMember = memberRepository.saveAndFlush(
                Member.ofKakao("kakao@example.com", "재가입회원", "same-kakao-sub")
        );

        assertThat(rejoinedMember.getId()).isNotEqualTo(withdrawnMemberId);
        assertThat(memberRepository.findByProviderAndProviderSub("kakao", "same-kakao-sub"))
                .contains(rejoinedMember);
    }

    @Test
    void legacyWithdrawnMemberActiveParticipationCanBeFoundForCleanup() {
        Member withdrawnMember = memberRepository.saveAndFlush(
                Member.ofLocal("legacy@example.com", "password", "기존탈퇴회원")
        );
        withdrawnMember.withdraw();
        memberRepository.flush();

        RoomParticipant staleParticipant = roomParticipantRepository.saveAndFlush(
                RoomParticipant.join(1L, withdrawnMember.getId(), 10_000_000L)
        );

        assertThat(roomParticipantRepository.findActiveParticipantsOfWithdrawnMembers())
                .extracting(RoomParticipant::getId)
                .containsExactly(staleParticipant.getId());
    }
}
