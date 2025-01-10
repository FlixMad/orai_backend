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
    public String createToken(String id, String departmentId) {
        Claims claims = Jwts.claims().setSubject(id); // 사용자 ID를 subject로 설정
        claims.put("departmentId", departmentId); // 부서 ID 추가
//        claims.get("departmentId", String.class);

        Date now = new Date();

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now) // 발급 시간
                .setExpiration(new Date(now.getTime() + expiration * 600 * 1000L)) // 만료 시간 (개발할 때 로그인 다시하기 귀찮으니까 10배 늘려놓음)
                .signWith(SignatureAlgorithm.HS256, secretKey) // 서명 알고리즘과 비밀 키 설정
                .compact();
    }

    /**
     * 리프레시 토큰 생성
     */
    public String createRefreshToken(String id, String departmentId) {
        Claims claims = Jwts.claims().setSubject(id); // 사용자 ID를 subject로 설정
        claims.put("departmentId", departmentId); // 부서 ID 추가
        Date now = new Date();

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now) // 발급 시간
                .setExpiration(new Date(now.getTime() + expirationRt * 60 * 1000L)) // 만료 시간
                .signWith(SignatureAlgorithm.HS256, secretKeyRt) // 서명 알고리즘과 리프레시 비밀 키 설정
                .compact();
    }


}
