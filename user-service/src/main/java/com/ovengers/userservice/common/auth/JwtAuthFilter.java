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

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 요청 헤더에서 사용자 정보를 가져옴
        String userEmail = request.getHeader("X-User-Email");
        String userAffId = request.getHeader("X-User-AffId");
        String userName = request.getHeader("X-User-Name");

        log.info("Incoming request - Email: {}, AffId: {}, Name: {}", userEmail, userAffId, userName);
        log.info("Request URL: {}", request.getRequestURI());

        try {
            // 헤더 값 검증
            if (StringUtils.hasText(userEmail) && StringUtils.hasText(userAffId)) {

                // 사용자 정보를 기반으로 TokenUserInfo 객체 생성
                TokenUserInfo userInfo = new TokenUserInfo(userEmail, userName);

                // 인증 객체 생성 (기본 권한 ROLE_USER 부여)
                Authentication auth = new UsernamePasswordAuthenticationToken(
                        userInfo, // 사용자 정보
                        "",       // 비밀번호 (빈 문자열)
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")) // 기본 권한
                );

                // SecurityContextHolder에 인증 정보 설정
                SecurityContextHolder.getContext().setAuthentication(auth);
                log.info("Authentication set for user: {}", userEmail);
            } else {
                log.warn("Missing or invalid headers - Email: {}, AffId: {}", userEmail, userAffId);
            }

            // 필터 체인 진행
            filterChain.doFilter(request, response);

        } catch (Exception e) {
            // 예외 처리
            log.error("Authentication error", e);
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json; charset=utf-8");
            response.getWriter().write("인증에 실패했습니다. 요청 헤더를 확인하세요.");
        }
    }
}
