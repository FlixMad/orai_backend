//package com.ovengers.userservice.common.auth;
//
//import org.jboss.aerogear.security.otp.Totp;
//import org.jboss.aerogear.security.otp.api.Base32;
//import org.springframework.stereotype.Component;
//
//@Component
//public class TotpUtil {
//
//    // MFA 비밀 키 생성
//    public String generateSecret() {
//        return Base32.random();  // Base32로 랜덤 비밀 키 생성
//    }
//
//    // MFA 코드 검증
//    public boolean verifyCode(String secret, String code) {
//        Totp totp = new Totp(secret);  // 비밀 키로 Totp 객체 생성
//        return totp.verify(code);  // 코드 검증
//    }
//}
