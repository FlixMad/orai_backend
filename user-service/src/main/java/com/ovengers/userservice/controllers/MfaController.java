//package com.ovengers.userservice.controllers;
//
//import com.warrenstrange.googleauth.GoogleAuthenticator;
//import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
//import com.ovengers.userservice.common.dto.CommonResDto;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.HashMap;
//import java.util.Map;
//
//@Slf4j
//@RestController
//@RequestMapping("/api/mfa")
//public class MfaController {
//
//    private final GoogleAuthenticator gAuth = new GoogleAuthenticator();
//
//    /**
//     * Mfa 시크릿 키 생성 및 반환
//     */
//    @GetMapping("/generate-key")
//    public ResponseEntity<CommonResDto<Map<String, String>>> generateKey(@RequestParam String email) {
//        GoogleAuthenticatorKey key = gAuth.createCredentials();
//        String secret = key.getKey();
//        String otpauthUrl = String.format("otpauth://totp/%s?secret=%s", email, secret);
//
//        Map<String, String> response = new HashMap<>();
//        response.put("secret", secret);
//        response.put("otpauthUrl", otpauthUrl);
//
//        log.info("Generated Mfa key for email {}: {}", email, secret);
//
//        return new ResponseEntity<>(new CommonResDto<>(HttpStatus.OK, "Mfa key generated.", response), HttpStatus.OK);
//    }
//
//    /**
//     * Mfa 인증 코드 검증
//     */
//    @PostMapping("/validate-code")
//    public ResponseEntity<CommonResDto<String>> validateCode(@RequestParam String secret, @RequestParam int code) {
//        boolean isValid = gAuth.authorize(secret, code);
//
//        if (isValid) {
//            log.info("Mfa code validated successfully.");
//            return new ResponseEntity<>(new CommonResDto<>(HttpStatus.OK, "Code is valid.", null), HttpStatus.OK);
//        } else {
//            log.warn("Invalid Mfa code provided.");
//            return new ResponseEntity<>(new CommonResDto<>(HttpStatus.UNAUTHORIZED, "Invalid code.", null), HttpStatus.UNAUTHORIZED);
//        }
//    }
//}
