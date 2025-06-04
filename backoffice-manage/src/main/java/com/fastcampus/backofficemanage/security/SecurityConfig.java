package com.fastcampus.backofficemanage.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtProvider jwtProvider;
    private final RedisTemplate<String, String> redisTemplate;

    /**
     * Spring Security의 필터 체인을 구성하여 인증 및 인가 정책을 설정합니다.
     *
     * 인증 없이 접근 가능한 엔드포인트(헬스 체크, 회원가입, 로그인, 토큰 재발급, 로그아웃, Swagger 문서 등)를 허용하고,
     * 그 외 모든 요청은 인증을 요구합니다. JWT 기반 인증을 위해 커스텀 JwtAuthenticationFilter를 UsernamePasswordAuthenticationFilter 앞에 추가합니다.
     *
     * @return 구성된 SecurityFilterChain 인스턴스
     * @throws Exception HttpSecurity 설정 중 오류가 발생할 경우
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/actuator/health",
                                "/merchants/register",
                                "/merchants/login",
                                "/merchants/reissue",
                                "/merchants/logout",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/v3/api-docs",
                                "/swagger-resources/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(new JwtAuthenticationFilter(jwtProvider, redisTemplate), // ✅ 수정
                        org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}