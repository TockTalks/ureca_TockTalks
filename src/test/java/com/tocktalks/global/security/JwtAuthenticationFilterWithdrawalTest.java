package com.tocktalks.global.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tocktalks.domain.auth.service.AccessTokenRevocationService;
import com.tocktalks.global.activity.ActiveMemberTracker;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 회원탈퇴로 폐기된 액세스 토큰이 인증에 사용되지 않는지 검증한다.
 */
@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterWithdrawalTest {

    @Mock private JwtProvider jwtProvider;
    @Mock private ActiveMemberTracker activeMemberTracker;
    @Mock private AccessTokenRevocationService accessTokenRevocationService;
    @Mock private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter filter;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void revokedMemberTokenDoesNotCreateAuthentication() throws Exception {
        MockHttpServletRequest request = requestWithBearerToken();
        MockHttpServletResponse response = new MockHttpServletResponse();
        when(jwtProvider.validateToken("access-token")).thenReturn(true);
        when(jwtProvider.isRefreshToken("access-token")).thenReturn(false);
        when(jwtProvider.getMemberId("access-token")).thenReturn(1L);
        when(accessTokenRevocationService.isRevoked(1L)).thenReturn(true);

        filter.doFilter(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(activeMemberTracker, never()).markActive(1L);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void activeMemberTokenStillCreatesAuthentication() throws Exception {
        MockHttpServletRequest request = requestWithBearerToken();
        MockHttpServletResponse response = new MockHttpServletResponse();
        when(jwtProvider.validateToken("access-token")).thenReturn(true);
        when(jwtProvider.isRefreshToken("access-token")).thenReturn(false);
        when(jwtProvider.getMemberId("access-token")).thenReturn(1L);
        when(accessTokenRevocationService.isRevoked(1L)).thenReturn(false);
        when(jwtProvider.getRole("access-token")).thenReturn("user");

        filter.doFilter(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        verify(activeMemberTracker).markActive(1L);
        verify(filterChain).doFilter(request, response);
    }

    private MockHttpServletRequest requestWithBearerToken() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer access-token");
        return request;
    }
}
