package com.ovengers.userservice.common.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
@Slf4j
@RequiredArgsConstructor
// 클라이언트가 전송한 토큰을 검사하는 필터
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Authorization 헤더에서 JWT 토큰 가져오기
        String token = request.getHeader("Authorization");

        if (StringUtils.hasText(token) && token.startsWith("Bearer ")) {
            token = token.substring(7);  // "Bearer " 부분 제거

            try {
                // JWT 토큰을 검증하고 사용자 정보를 설정
                TokenUserInfo userInfo = jwtTokenProvider.validateAndGetTokenUserInfo(token);

                // 사용자 인증 정보를 SecurityContext에 설정
                Authentication auth = new UsernamePasswordAuthenticationToken(
                        userInfo, // 사용자 정보
                        null, // 비밀번호는 null
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
                );

                // 인증 정보 설정
                SecurityContextHolder.getContext().setAuthentication(auth);
                log.info("User authenticated: {}", userInfo.getId());
            } catch (Exception e) {
                log.error("JWT token validation failed", e);
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.getWriter().write("Invalid or expired JWT token.");
                return;  // 인증 실패 시 후속 필터 실행 중단
            }
        }

        // 필터 체인 진행
        filterChain.doFilter(request, response);
    }
}
