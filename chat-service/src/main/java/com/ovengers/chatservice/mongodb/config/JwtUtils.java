package com.ovengers.chatservice.mongodb.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;


@Slf4j
@Component
public class JwtUtils {

    // 토큰 서명에 사용할 비밀키
    @Value("${jwt.secretKey}")
    private String secretKey;

    /**
     * JWT 토큰에서 클레임 추출
     * @param token JWT 토큰
     * @return 클레임
     */
    public Claims extractClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            log.error("JWT validation failed: {}", e.getMessage());
            return null;
        }
    }

    /**
     * JWT 토큰에서 사용자 ID 추출
     * @param token JWT 토큰
     * @return 사용자 ID
     */
    public String getUserIdFromToken(String token) {
        return extractClaims(token).getSubject(); // 토큰의 subject에서 userId 추출
    }

    /**
     * JWT 토큰에서 부서 ID 추출
     * @param token JWT 토큰
     * @return 부서 ID
     */
    public String getDepartmentFromToken(String token) {
        return extractClaims(token).get("departmentId", String.class);
    }

    /**
     * JWT 토큰 유효성 검사
     * @param token JWT 토큰
     * @return 유효한 토큰이면 true, 아니면 false
     */
    public boolean validateToken(String token) {
        try {
            extractClaims(token); // 클레임 추출 중 예외가 발생하지 않으면 유효한 토큰
            return true;
        } catch (Exception e) {
            return false; // 유효하지 않은 토큰
        }
    }


}