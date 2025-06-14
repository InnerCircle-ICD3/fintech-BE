package com.fastcampus.appusermanage.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

import static com.fastcampus.common.constant.RedisKeys.BLOCKLIST_PREFIX;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final RedisTemplate<String, String> redisTemplate;

    public JwtAuthenticationFilter(JwtProvider jwtProvider, RedisTemplate<String, String> redisTemplate) {
        this.jwtProvider = jwtProvider;
        this.redisTemplate = redisTemplate;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        String uri = request.getRequestURI();

        // Swagger 및 인증이 필요없는 경로는 그대로 통과
        if (uri.startsWith("/swagger-ui")
                || uri.equals("/swagger-ui.html")
                || uri.startsWith("/v3/api-docs")
                || uri.startsWith("/swagger-resources")
                || uri.equals("/app-users/register")
                || uri.equals("/app-users/login")
                || uri.equals("/app-users/reissue")
                || uri.equals("/app-users/logout")
                || uri.startsWith("/api-docs")) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            // 로그아웃된 토큰(블랙리스트)이라면 인증정보를 클리어
            if (Boolean.TRUE.equals(redisTemplate.hasKey(BLOCKLIST_PREFIX + token))) {
                SecurityContextHolder.clearContext();
            } else if (jwtProvider.validateToken(token)) {
                String loginId = jwtProvider.getSubject(token);
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(loginId, null, List.of());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                // 토큰이 유효하지 않은 경우에도 인증정보를 클리어
                SecurityContextHolder.clearContext();
            }
        } else {
            // Authorization 헤더가 없으면 인증정보를 클리어
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}
