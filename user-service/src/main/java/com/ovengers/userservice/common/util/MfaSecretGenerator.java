package com.ovengers.userservice.common.util;
import com.warrenstrange.googleauth.GoogleAuthenticator;

public class MfaSecretGenerator {
    public static String generateSecret() {
        GoogleAuthenticator gAuth = new GoogleAuthenticator();
        return gAuth.createCredentials().getKey();  // Google Authenticator 비밀 키 생성
    }
}

