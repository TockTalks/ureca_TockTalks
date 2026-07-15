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
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/health", "/api/auth/kakao/**", "/api/auth/signup", "/api/auth/login").permitAll()
                .requestMatchers("/api/auth/me").authenticated()
                // "/api/rooms/*"가 한 세그먼트라 "/mine"도 매칭되므로, permitAll 와일드카드보다 먼저 인증을 걸어야 한다
                .requestMatchers(HttpMethod.GET, "/api/rooms/mine").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/rooms", "/api/rooms/*", "/api/rooms/*/ranking").permitAll()
                .requestMatchers("/api/rooms/**").authenticated()
                // 개발 초기엔 나머지는 열어두고, 인증 붙이면서 하나씩 잠그기 (AUTH-06)
                .anyRequest().permitAll()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
