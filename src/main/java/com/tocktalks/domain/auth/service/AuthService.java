package com.tocktalks.domain.auth.service;

import com.tocktalks.domain.auth.client.KakaoOAuthClient;
import com.tocktalks.domain.auth.dto.KakaoTokenResponse;
import com.tocktalks.domain.auth.dto.KakaoUserInfoResponse;
import com.tocktalks.domain.auth.dto.LoginRequest;
import com.tocktalks.domain.auth.dto.SignupRequest;
import com.tocktalks.domain.auth.dto.TokenResponse;
import com.tocktalks.domain.member.entity.Member;
import com.tocktalks.domain.member.repository.MemberRepository;
import com.tocktalks.domain.room.service.RoomService;
import com.tocktalks.global.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final String PROVIDER_KAKAO = "kakao";
    private static final String PROVIDER_LOCAL = "local";

    private final KakaoOAuthClient kakaoOAuthClient;
    private final MemberRepository memberRepository;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;
    private final RoomService roomService;

    @Transactional
    public TokenResponse signup(SignupRequest request) {
        if (memberRepository.findByEmail(request.email()).isPresent()) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }

        Member member = memberRepository.save(Member.ofLocal(
                request.email(),
                passwordEncoder.encode(request.password()),
                request.nickname()));
        roomService.joinDefaultRoom(member.getId());

        String accessToken = jwtProvider.createAccessToken(member.getId(), member.getRole());
        String refreshToken = jwtProvider.createRefreshToken(member.getId(), member.getRole());
        return new TokenResponse(accessToken, refreshToken, member.getId(), member.getNickname(), true);
    }

    public TokenResponse loginWithLocal(LoginRequest request) {
        Member member = memberRepository.findByEmail(request.email())
                .filter(m -> PROVIDER_LOCAL.equals(m.getProvider()))
                .orElseThrow(() -> new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다."));

        if (!passwordEncoder.matches(request.password(), member.getPassword())) {
            throw new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다.");
        }

        String accessToken = jwtProvider.createAccessToken(member.getId(), member.getRole());
        String refreshToken = jwtProvider.createRefreshToken(member.getId(), member.getRole());
        return new TokenResponse(accessToken, refreshToken, member.getId(), member.getNickname(), false);
    }

    @Transactional
    public TokenResponse loginWithKakao(String code) {
        KakaoTokenResponse kakaoToken = kakaoOAuthClient.getToken(code);
        KakaoUserInfoResponse userInfo = kakaoOAuthClient.getUserInfo(kakaoToken.accessToken());

        String providerSub = String.valueOf(userInfo.id());
        boolean isNewMember = false;

        Member member = memberRepository.findByProviderAndProviderSub(PROVIDER_KAKAO, providerSub)
                .orElse(null);

        if (member == null) {
            member = memberRepository.save(Member.ofKakao(
                    resolveEmail(userInfo, providerSub),
                    resolveNickname(userInfo),
                    providerSub));
            roomService.joinDefaultRoom(member.getId());
            isNewMember = true;
        }

        String accessToken = jwtProvider.createAccessToken(member.getId(), member.getRole());
        String refreshToken = jwtProvider.createRefreshToken(member.getId(), member.getRole());

        return new TokenResponse(accessToken, refreshToken, member.getId(), member.getNickname(), isNewMember);
    }

    private String resolveEmail(KakaoUserInfoResponse userInfo, String providerSub) {
        if (userInfo.kakaoAccount() != null && userInfo.kakaoAccount().email() != null) {
            return userInfo.kakaoAccount().email();
        }
        return "kakao_" + providerSub + "@kakao.local";
    }

    public Member getMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
    }

    public boolean isEmailAvailable(String email) {
        return memberRepository.findByEmail(email).isEmpty();
    }

    private String resolveNickname(KakaoUserInfoResponse userInfo) {
        if (userInfo.kakaoAccount() != null && userInfo.kakaoAccount().profile() != null
                && userInfo.kakaoAccount().profile().nickname() != null) {
            return userInfo.kakaoAccount().profile().nickname();
        }
        return "카카오사용자";
    }
}
