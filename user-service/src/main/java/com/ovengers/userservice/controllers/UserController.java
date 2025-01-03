package com.ovengers.userservice.controllers;

import com.ovengers.userservice.common.auth.JwtTokenProvider;
import com.ovengers.userservice.dto.UserRequestDto;
import com.ovengers.userservice.dto.UserResponseDto;
import com.ovengers.userservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;


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
