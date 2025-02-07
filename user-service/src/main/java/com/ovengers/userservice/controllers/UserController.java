package com.ovengers.userservice.controllers;

import com.ovengers.userservice.common.auth.JwtTokenProvider;
import com.ovengers.userservice.common.dto.CommonErrorDto;
import com.ovengers.userservice.common.dto.CommonResDto;
import com.ovengers.userservice.common.util.MfaSecretGenerator;
import com.ovengers.userservice.dto.LoginRequestDto;
import com.ovengers.userservice.dto.UserRequestDto;
import com.ovengers.userservice.dto.UserResponseDto;
import com.ovengers.userservice.service.UserService;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate<String, Object> redisTemplate;
    private final GoogleAuthenticator gAuth = new GoogleAuthenticator();


    //회원가입하면서 mfa시크릿 키 생성

    @PostMapping("/create")
    public ResponseEntity<UserResponseDto> createUser(@Valid @RequestBody UserRequestDto userRequestDto) {
        // 1. MFA 시크릿 키 생성
        String mfaSecret = MfaSecretGenerator.generateSecret();
        log.info("Generated MFA secret for user {}: {}", userRequestDto.getEmail(), mfaSecret);

        // 2. UserRequestDto에 MFA 시크릿 키 설정
        userRequestDto.setMfaSecret(mfaSecret);

        // 3. User 생성 및 저장
        UserResponseDto responseDto = userService.createUser(userRequestDto);

        // 4. 클라이언트로 응답 반환
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }


    /**
     * 로그인 처리 (JWT 토큰 발급 및 Mfa 데이터 반환)
     */
    @PostMapping("/login")
    public ResponseEntity<CommonResDto<Map<String, Object>>> login(@Valid @RequestBody UserRequestDto userRequestDto) {
        UserResponseDto responseDto = userService.login(userRequestDto);

        // MFA 시크릿 키 조회
        String secret = userService.getUserSecret(userRequestDto.getEmail());
        Map<String, Object> result = new HashMap<>();
        result.put("secret", secret);

        return new ResponseEntity<>(
                new CommonResDto<>(HttpStatus.OK, "Login successful, Mfa required.", result),
                HttpStatus.OK
        );
    }
    @PostMapping("/devLogin")
    public ResponseEntity<CommonResDto<Map<String, Object>>> devLogin(@Valid @RequestBody LoginRequestDto reqDto) {
        UserResponseDto responseDto = userService.login(reqDto);

        return new ResponseEntity<>(
                new CommonResDto<>(HttpStatus.OK, "Login successful", responseDto),
                HttpStatus.OK
        );
    }

    /**
     * Mfa 인증 코드 검증 및 JWT 토큰 발급
     * 'email' -> 'secret'로 변경
     */
    @PostMapping("/validate-mfa")
    public ResponseEntity<CommonResDto<Map<String, Object>>> validateMfa(
            @RequestParam String secret,  // 'email' -> 'secret'
            @RequestParam String code) {  // 'code'를 String으로 변경
        boolean isValid = gAuth.authorize(secret, Integer.parseInt(code));  // 코드 값은 Integer로 변환

        if (isValid) {
            // secret으로 사용자 조회
            UserResponseDto user = userService.getUserBySecret(secret);
            String token = jwtTokenProvider.createToken(user.getUserId(), user.getEmail(), user.getDepartmentId());
            user.setToken(token);

            return new ResponseEntity<>(
                    new CommonResDto<>(HttpStatus.OK, "Mfa validated successfully.", user),
                    HttpStatus.OK
            );
        } else {
            return new ResponseEntity<>(
                    new CommonResDto<>(HttpStatus.UNAUTHORIZED, "Invalid Mfa code.", null),
                    HttpStatus.UNAUTHORIZED
            );
        }
    }




    /**
     * Mfa 인증 코드 검증
     */
    @PostMapping("/mfa/validate-code")
    public ResponseEntity<CommonResDto<String>> validateCode(@RequestParam String secret, @RequestParam int code) {
        boolean isValid = gAuth.authorize(secret, code);

        if (isValid) {
            log.info("Mfa code validated successfully.");
            return new ResponseEntity<>(new CommonResDto<>(HttpStatus.OK, "Code is valid.", null), HttpStatus.OK);
        } else {
            log.warn("Invalid Mfa code provided.");
            return new ResponseEntity<>(new CommonResDto<>(HttpStatus.UNAUTHORIZED, "Invalid code.", null), HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * 토큰 갱신
     */
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> id) {
        log.info("/api/users/refresh: POST, id : {}", id.get("id"));
        UserResponseDto user = userService.getUserById(id.get("id"));

        Object obj = redisTemplate.opsForValue().get(user.getUserId());
        log.info("레디스에서 조회한 데이터: {}", obj);

        if (obj == null) {
            log.info("Refresh token expired.");
            return new ResponseEntity<>(new CommonErrorDto(HttpStatus.UNAUTHORIZED, "EXPIRED_RT"), HttpStatus.UNAUTHORIZED);
        }
        // 새로운 access token을 발급하자.
        String newAccessToken
                = jwtTokenProvider.createToken(user.getUserId(),user.getEmail(),user.getDepartmentId());

        Map<String, Object> result = new HashMap<>();
        result.put("token", newAccessToken);

        return new ResponseEntity<>(new CommonResDto<>(HttpStatus.OK, "New token issued.", result), HttpStatus.OK);
    }

    /**
     * 내 정보 조회
     */
    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> getMyInfo() {
        UserResponseDto responseDto = userService.getMyInfo();
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    /**
     * 특정 사용자 조회
     */
    @GetMapping("/{userId}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable String userId) {
        UserResponseDto responseDto = userService.getUserById(userId);
        log.info("user:"+responseDto);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    /**
     * 비밀번호 변경
     */
    @PutMapping("/change-password")
    public ResponseEntity<String> changePassword(
            @RequestParam String userId,
            @RequestParam String currentPassword,
            @RequestParam String newPassword) {
        userService.changePassword(userId, currentPassword, newPassword);
        return new ResponseEntity<>("Password changed successfully.", HttpStatus.OK);
    }

    /**
     * 이메일 중복 체크
     */
    @GetMapping("/check-email")
    public ResponseEntity<Boolean> isEmailDuplicate(@RequestParam String email) {
        boolean isDuplicate = userService.isEmailDuplicate(email);
        return new ResponseEntity<>(isDuplicate, HttpStatus.OK);
    }

    @PostMapping("/list")
    public ResponseEntity<List<UserResponseDto>> getUsersByIds(@RequestBody List<String> userIds) {
        List<UserResponseDto> users = userService.getUsersByIds(userIds);
        return ResponseEntity.ok(users);
    }
}
