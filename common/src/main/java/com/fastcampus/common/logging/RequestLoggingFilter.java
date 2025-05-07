package com.fastcampus.common.logging;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Slf4j
public class RequestLoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String traceId = UUID.randomUUID().toString().substring(0, 8);
        MDC.put("traceId", traceId);

        log.info("[{}] → {} {}", traceId, request.getMethod(), request.getRequestURI());

        long start = System.currentTimeMillis();
        filterChain.doFilter(request, response);
        long duration = System.currentTimeMillis() - start;

        log.info("[{}] ← {} {} in {} ms", traceId, request.getMethod(), request.getRequestURI(), duration);
        MDC.clear();
    }
}
