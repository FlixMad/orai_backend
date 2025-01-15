package com.ovengers.chatservice.mongodb.config;

import com.ovengers.chatservice.common.auth.TokenUserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtChannelInterceptor implements ChannelInterceptor {

    private final JwtUtils jwtUtils; // JwtUtils 주입

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if ("CONNECT".equals(accessor.getCommand().name())) {
            String jwtToken = accessor.getFirstNativeHeader("Authorization");
            if (jwtToken != null && jwtToken.startsWith("Bearer ")) {
                jwtToken = jwtToken.substring(7); // "Bearer " 제거

                try {
                    // JwtUtils를 사용하여 토큰 검증 및 정보 추출
                    String userId = jwtUtils.getUserIdFromToken(jwtToken);
                    String departmentId = jwtUtils.getDepartmentFromToken(jwtToken);

                    // 인증 정보 설정
                    List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
                    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                            new TokenUserInfo(userId, departmentId), null, authorities);
                    SecurityContextHolder.getContext().setAuthentication(auth);
                } catch (Exception e) {
                    throw new IllegalArgumentException("Invalid JWT Token: " + e.getMessage());
                }
            }
        }

        return message;
    }
}
