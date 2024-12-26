package com.ovengers.userservice.common.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Slf4j
public class JwtTokenProvider {

    @Value("${jwt.secretKey}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private int expiration;

    @Value("${jwt.secretKeyRt}")
    private String secretKeyRt;

    @Value("${jwt.expirationRt}")
    private int expirationRt;

    /**
     * 액세스 토큰 생성
     */
    public String createToken(String id, String affId, String name) {
        Claims claims = Jwts.claims().setSubject(id); // 사용자 ID를 subject로 설정
        claims.put("affId", affId); // 소속 ID 추가
        claims.put("name", name);  // 사용자 이름 추가
        Date now = new Date();

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now) // 발급 시간
                .setExpiration(new Date(now.getTime() + expiration * 60 * 1000L)) // 만료 시간
                .signWith(SignatureAlgorithm.HS256, secretKey) // 서명 알고리즘과 비밀 키 설정
                .compact();
    }

    /**
     * 리프레시 토큰 생성
     */
    public String createRefreshToken(String id, String affId, String name) {
        Claims claims = Jwts.claims().setSubject(id); // 사용자 ID를 subject로 설정
        claims.put("affId", affId); // 소속 ID 추가
        claims.put("name", name);  // 사용자 이름 추가
        Date now = new Date();

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now) // 발급 시간
                .setExpiration(new Date(now.getTime() + expirationRt * 60 * 1000L)) // 만료 시간
                .signWith(SignatureAlgorithm.HS256, secretKeyRt) // 서명 알고리즘과 리프레시 비밀 키 설정
                .compact();
    }

    /**
     * 토큰 검증 및 사용자 정보 반환
     */
    public TokenUserInfo validateAndGetTokenUserInfo(String token) throws Exception {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey) // 액세스 토큰의 비밀 키로 파싱
                .build()
                .parseClaimsJws(token)
                .getBody();

        log.info("Parsed claims: {}", claims);

        return TokenUserInfo.builder()
                .id(claims.getSubject()) // 사용자 ID
//                .affId(claims.get("affId", String.class)) // 소속 ID
                .name(claims.get("name", String.class))  // 사용자 이름
                .build();
    }
}
