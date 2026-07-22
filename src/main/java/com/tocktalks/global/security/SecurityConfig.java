package com.tocktalks.global.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> {})
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(exception -> exception
                    .authenticationEntryPoint((request, response, authenticationException) ->
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED))
                    .accessDeniedHandler((request, response, accessDeniedException) ->
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN)))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/health", "/ws/**").permitAll()
                .requestMatchers("/api/auth/kakao/**", "/api/auth/signup", "/api/auth/login",
                        "/api/auth/check-email", "/api/auth/reissue").permitAll()
                .requestMatchers("/api/auth/me").authenticated()
                // "/api/rooms/*"가 한 세그먼트라 "/mine"도 매칭되므로, permitAll 와일드카드보다 먼저 인증을 걸어야 한다
                .requestMatchers(HttpMethod.GET, "/api/rooms/mine").authenticated()
                .requestMatchers(HttpMethod.GET,
                        "/api/rooms", "/api/rooms/*", "/api/rooms/*/ranking", "/api/rooms/*/rankings/**").permitAll()
                .requestMatchers("/api/rooms/**").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/price/**").permitAll()
                // 커뮤니티는 조회(@LoginMemberId)도 로그인이 필요하고, 포트폴리오는 개인 자산 정보라 로그인 필요
                .requestMatchers("/api/posts/**", "/api/portfolios/**").authenticated()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                // 도메인별로 다 잠갔으니 나머지 신규 API는 기본적으로 인증 필요 (AUTH-06)
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
