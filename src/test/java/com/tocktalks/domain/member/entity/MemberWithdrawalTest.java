package com.tocktalks.domain.member.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

/**
 * 회원탈퇴 익명화가 기존 회원 행을 유지하면서 개인정보를 제거하는지 검증한다.
 */
class MemberWithdrawalTest {

    @Test
    void localMemberIsAnonymizedAndMarkedWithdrawn() {
        Member member = Member.ofLocal("user@example.com", "encoded-password", "사용자");

        member.withdraw();

        assertThat(member.getEmail())
                .startsWith("withdrawn_")
                .endsWith("@withdrawn.local")
                .doesNotContain("user@example.com");
        assertThat(member.getPassword()).isNull();
        assertThat(member.getNickname()).isEqualTo("탈퇴한 회원");
        assertThat(member.getProviderSub()).isNull();
        assertThat(member.getStatus()).isEqualTo("withdrawn");
        assertThat(member.isWithdrawn()).isTrue();
    }

    @Test
    void withdrawnMemberCannotBeBlocked() {
        Member member = Member.ofLocal("user@example.com", "encoded-password", "사용자");
        member.withdraw();

        assertThatThrownBy(member::block)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("탈퇴한 회원은 차단할 수 없습니다.");
    }
}
