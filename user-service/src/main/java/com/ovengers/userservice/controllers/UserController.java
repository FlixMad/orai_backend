package com.ovengers.userservice.controllers;

import com.ovengers.userservice.common.auth.JwtTokenProvider;
import com.ovengers.userservice.common.dto.CommonErrorDto;
import com.ovengers.userservice.common.dto.CommonResDto;
import com.ovengers.userservice.dto.UserRequestDto;
import com.ovengers.userservice.dto.UserResponseDto;
import com.ovengers.userservice.entity.User;
import com.ovengers.userservice.service.UserService;
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
    private final RedisTemplate<String , Object> redisTemplate;


    /**
     * 사용자 등록
     */

    @PostMapping("/create")
    public ResponseEntity<UserResponseDto> createUser(@Valid @RequestBody UserRequestDto userRequestDto) {
        UserResponseDto responseDto = userService.createUser(userRequestDto);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    /**
     * 로그인 처리 (JWT 토큰 발급)
     */
    @PostMapping("/login")
    public ResponseEntity<UserResponseDto> login(@Valid @RequestBody UserRequestDto userRequestDto) {
        UserResponseDto responseDto = userService.login(userRequestDto);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> id) {
        log.info("/api/users/refresh: POST, id : {}", id.get("id"));
        UserResponseDto user = userService.getUserById(id.get("id"));
        // email로 redis를 조회해서 refresh token을 가져오자
        Object obj = redisTemplate.opsForValue().get(user.getUserId());
        log.info("레디스에서 조회한 데이터: {}", obj);
        if (obj == null) { // refresh token의 수명이 다됨.
            log.info("refresh 만료!");

            return new ResponseEntity<>(new CommonErrorDto(
                    HttpStatus.UNAUTHORIZED,
                    "EXPIRED_RT"
            ), HttpStatus.UNAUTHORIZED);

        }
        // 새로운 access token을 발급하자.
        String newAccessToken
                = jwtTokenProvider.createToken(user.getUserId(),user.getDepartmentId());

        Map<String, Object> info = new HashMap<>();
        info.put("token", newAccessToken);

        CommonResDto resDto
                = new CommonResDto(HttpStatus.OK, "새 토큰 발급됨!", info);

        return new ResponseEntity<>(resDto, HttpStatus.OK);

    }

    /**
     * 내 정보 조회
     */
    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> getMyInfo() {
        UserResponseDto responseDto = userService.getMyInfo();
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

//    /**
//     * 모든 사용자 조회
//     */
//    @GetMapping
//    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
//        List<UserResponseDto> users = userService.getAllUsers();
//        return new ResponseEntity<>(users, HttpStatus.OK);
//    }

    /**http://localhost:8181/api/attitude/checkin
     * 특정 사용자 조회
     */
    @GetMapping("/{userId}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable String userId) {  // 파라미터를 String으로 수정
        UserResponseDto responseDto = userService.getUserById(userId);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    /**
     * 비밀번호 변경
     */

    @PutMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestParam String userId,  // 파라미터를 String으로 수정
                                                 @RequestParam String currentPassword,
                                                 @RequestParam String newPassword) {
        userService.changePassword(userId, currentPassword, newPassword);
        return new ResponseEntity<>("비밀번호가 변경되었습니다.", HttpStatus.OK);
    }
    /**
     * 이메일 중복 체크
     */
    @GetMapping("/check-email")
    public ResponseEntity<Boolean> isEmailDuplicate(@RequestParam String email) {
        boolean isDuplicate = userService.isEmailDuplicate(email);
        return new ResponseEntity<>(isDuplicate, HttpStatus.OK);
    }
}
