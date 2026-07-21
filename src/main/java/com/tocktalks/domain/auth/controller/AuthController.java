package com.tocktalks.domain.auth.controller;

import com.tocktalks.domain.auth.client.KakaoOAuthClient;
import com.tocktalks.domain.auth.dto.EmailCheckResponse;
import com.tocktalks.domain.auth.dto.KakaoLoginRequest;
import com.tocktalks.domain.auth.dto.LoginRequest;
import com.tocktalks.domain.auth.dto.MemberUpdateRequest;
import com.tocktalks.domain.auth.dto.ReissueRequest;
import com.tocktalks.domain.auth.dto.SignupRequest;
import com.tocktalks.domain.auth.dto.TokenResponse;
import com.tocktalks.domain.auth.service.AuthService;
import com.tocktalks.domain.member.entity.Member;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Validated
public class AuthController {

    private final AuthService authService;
    private final KakaoOAuthClient kakaoOAuthClient;

    // Postman 테스트용: 브라우저로 이 URL을 열어 카카오 로그인 후 redirect_uri 쪽 code 쿼리파라미터를 복사해서 /kakao/login 에 사용
    @GetMapping("/kakao/authorize-url")
    public Map<String, String> kakaoAuthorizeUrl() {
        return Map.of("url", kakaoOAuthClient.buildAuthorizeUrl());
    }

    @PostMapping("/kakao/login")
    public TokenResponse kakaoLogin(@RequestBody @Valid KakaoLoginRequest request) {
        return authService.loginWithKakao(request.code());
    }

    @GetMapping("/check-email")
    public EmailCheckResponse checkEmail(@RequestParam @NotBlank @Email String email) {
        return new EmailCheckResponse(authService.isEmailAvailable(email));
    }

    @PostMapping("/signup")
    public TokenResponse signup(@RequestBody @Valid SignupRequest request) {
        return authService.signup(request);
    }

    @PostMapping("/login")
    public TokenResponse login(@RequestBody @Valid LoginRequest request) {
        return authService.loginWithLocal(request);
    }

    @GetMapping("/me")
    public Map<String, Object> me(Authentication authentication) {
        Long memberId = (Long) authentication.getPrincipal();
        Member member = authService.getMember(memberId);
        return Map.of(
                "id", member.getId(),
                "email", member.getEmail(),
                "nickname", member.getNickname(),
                "role", member.getRole()
        );
    }

    @PatchMapping("/me")
    public void updateMe(Authentication authentication, @RequestBody @Valid MemberUpdateRequest request) {
        authService.updateMember((Long) authentication.getPrincipal(), request);
    }

    @PostMapping("/reissue")
    public TokenResponse reissue(@RequestBody @Valid ReissueRequest request) {
        return authService.reissue(request.refreshToken());
    }

    @PostMapping("/logout")
    public void logout(Authentication authentication) {
        authService.logout((Long) authentication.getPrincipal());
    }
}
