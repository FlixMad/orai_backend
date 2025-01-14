package com.ovengers.chatservice.mongodb.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {

    // 토큰 서명에 사용할 비밀키
    private final Key secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    /**
     * JWT 토큰 생성
     * @param userId 사용자 ID
     * @param departmentId 부서 ID
     * @return 생성된 JWT 토큰
     */
    public String generateToken(String userId, String departmentId) {
        // 토큰의 유효시간 (예: 1시간)
        long TOKEN_VALIDITY = 60 * 60 * 1000L;
        return Jwts.builder()
                .setSubject(userId) // 사용자 ID를 주제로 설정
                .claim("departmentId", departmentId) // 부서 ID를 클레임으로 추가
                .setIssuedAt(new Date()) // 토큰 발행 시간
                .setExpiration(new Date(System.currentTimeMillis() + TOKEN_VALIDITY)) // 토큰 만료 시간
                .signWith(secretKey) // 비밀키로 서명
                .compact();
    }

    /**
     * JWT 토큰에서 클레임 추출
     * @param token JWT 토큰
     * @return 클레임
     */
    public Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey) // 비밀키 설정
                .build()
                .parseClaimsJws(token) // 토큰 파싱 및 서명 검증
                .getBody();
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

    public String extractUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(secretKey) // 시크릿 키 설정
                .parseClaimsJws(token)
                .getBody();

        return claims.get("id", String.class); // ID 추출
    }
}
