package com.ovengers.calendarservice.common.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
// 클라이언트가 전송한 토큰을 검사하는 필터
public class JwtAuthFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 게이트웨이가 토큰 내에 클레임을 헤더에 담아서 보내준다.
        String userId = request.getHeader("X-User-Id");
        String departmentId = request.getHeader("X-Department-Id");

        log.info(": {}", userId);
        log.info("request Url: {}", request.getRequestURI());
        // 토큰 위조검사 및 인증 완료
        if (userId != null){
            // 인증 완료 처리
            // spring security에게 인증 정보를 전달해서 전역적으로 어플리케이션 내에서
            // 인증 정보를 활용할 수 있도록 설정.
            List<SimpleGrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
            Authentication auth = new UsernamePasswordAuthenticationToken(
                    new TokenUserInfo(userId,departmentId),// 컨트롤러 등에서 활용할 유저 정보
                    "" // 인증된 사용자 비밀번호: 보통 null 혹은 빈 문자열로 선언.
                    ,authorities
            );

            // 시큐리티 컨테이너에 인증 정보 객체 등록
            SecurityContextHolder.getContext().setAuthentication(auth);

        }


        // 필터 체인 진행
        filterChain.doFilter(request, response);
    }
}
