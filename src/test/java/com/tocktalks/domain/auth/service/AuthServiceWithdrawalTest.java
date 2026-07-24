package com.tocktalks.domain.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tocktalks.domain.auth.client.KakaoOAuthClient;
import com.tocktalks.domain.auth.dto.MemberWithdrawalRequest;
import com.tocktalks.domain.member.entity.Member;
import com.tocktalks.domain.member.repository.FavoriteStockRepository;
import com.tocktalks.domain.member.repository.MemberRepository;
import com.tocktalks.domain.room.service.RoomService;
import com.tocktalks.global.security.JwtProvider;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 회원탈퇴 서비스가 본인 확인과 데이터·토큰 정리를 안전하게 수행하는지 검증한다.
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceWithdrawalTest {

    @Mock private KakaoOAuthClient kakaoOAuthClient;
    @Mock private MemberRepository memberRepository;
    @Mock private FavoriteStockRepository favoriteStockRepository;
    @Mock private JwtProvider jwtProvider;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private RoomService roomService;
    @Mock private RefreshTokenService refreshTokenService;
    @Mock private AccessTokenRevocationService accessTokenRevocationService;
    @Mock private LoginAttemptService loginAttemptService;

    @InjectMocks
    private AuthService authService;

    @Test
    void localMemberWithdrawalRequiresCurrentPasswordAndRevokesTokens() {
        Member member = Member.ofLocal("user@example.com", "encoded-password", "사용자");
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(passwordEncoder.matches("correct-password", "encoded-password")).thenReturn(true);

        authService.withdraw(1L, new MemberWithdrawalRequest("correct-password"));

        assertThat(member.isWithdrawn()).isTrue();
        verify(roomService).endActiveParticipationsForWithdrawal(1L);
        verify(favoriteStockRepository).deleteAllByMemberId(1L);
        verify(refreshTokenService).delete(1L);
        verify(accessTokenRevocationService).revoke(1L);
    }

    @Test
    void localMemberWithdrawalRejectsWrongPasswordWithoutChangingData() {
        Member member = Member.ofLocal("user@example.com", "encoded-password", "사용자");
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(passwordEncoder.matches("wrong-password", "encoded-password")).thenReturn(false);

        assertThatThrownBy(() ->
                authService.withdraw(1L, new MemberWithdrawalRequest("wrong-password")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("현재 비밀번호가 올바르지 않습니다.");

        assertThat(member.isWithdrawn()).isFalse();
        verify(roomService, never()).endActiveParticipationsForWithdrawal(1L);
        verify(favoriteStockRepository, never()).deleteAllByMemberId(1L);
        verify(refreshTokenService, never()).delete(1L);
        verify(accessTokenRevocationService, never()).revoke(1L);
    }

    @Test
    void kakaoMemberWithdrawalDoesNotRequireLocalPassword() {
        Member member = Member.ofKakao("kakao@example.com", "카카오사용자", "kakao-sub");
        when(memberRepository.findById(2L)).thenReturn(Optional.of(member));

        authService.withdraw(2L, new MemberWithdrawalRequest(null));

        assertThat(member.isWithdrawn()).isTrue();
        verify(roomService).endActiveParticipationsForWithdrawal(2L);
        verify(favoriteStockRepository).deleteAllByMemberId(2L);
        verify(refreshTokenService).delete(2L);
        verify(accessTokenRevocationService).revoke(2L);
    }
}
