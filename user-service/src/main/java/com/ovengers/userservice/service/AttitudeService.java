package com.ovengers.userservice.service;

import com.ovengers.userservice.common.auth.JwtTokenProvider;
import com.ovengers.userservice.dto.AttitudeResponseDto;
import com.ovengers.userservice.entity.Attitude;
import com.ovengers.userservice.entity.User;
import com.ovengers.userservice.repository.AttitudeRepository;
import com.ovengers.userservice.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;

@Service
public class AttitudeService {

    private final AttitudeRepository attitudeRepository;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public AttitudeService(AttitudeRepository attitudeRepository, UserRepository userRepository, JwtTokenProvider jwtTokenProvider) {
        this.attitudeRepository = attitudeRepository;
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    // 출근 기록
    public AttitudeResponseDto recordCheckIn() {
        // HTTP 요청에서 JWT 토큰 추출
        String userId = getUserIdFromRequest();

        // 사용자 조회
        User user = userRepository.findByUserId(userId) // userId로 사용자 찾기
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 출근 기록 생성
        Attitude attitude = Attitude.builder()
                .checkInTime(LocalDateTime.now())  // 현재 시간으로 출근 시간 설정
                .checkOutTime(null) // 퇴근 시간은 아직 없음
                .user(user)
                .build();

        Attitude savedAttitude = attitudeRepository.save(attitude);

        // 반환 DTO 생성
        return AttitudeResponseDto.builder()
                .attitudeId(savedAttitude.getAttitudeId())
                .createdAt(savedAttitude.getCreatedAt())
                .checkInTime(savedAttitude.getCheckInTime())
                .checkOutTime(savedAttitude.getCheckOutTime())
                .userId(user.getUserId())
                .userName(user.getName())
                .build();
    }

    // 퇴근 기록
    public AttitudeResponseDto recordCheckOut() {
        // HTTP 요청에서 JWT 토큰 추출
        String userId = getUserIdFromRequest();

        // 사용자 조회
        User user = userRepository.findByUserId(userId) // userId로 사용자 찾기
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 출근 기록 조회
        Attitude attitude = attitudeRepository.findTopByUserOrderByCreatedAtDesc(user)
                .orElseThrow(() -> new IllegalArgumentException("출근 기록이 없습니다."));

        // 퇴근 기록 업데이트
        attitude.setCheckOutTime(LocalDateTime.now());  // 현재 시간으로 퇴근 시간 설정
        Attitude savedAttitude = attitudeRepository.save(attitude);

        // 반환 DTO 생성
        return AttitudeResponseDto.builder()
                .attitudeId(savedAttitude.getAttitudeId())
                .createdAt(savedAttitude.getCreatedAt())
                .checkInTime(savedAttitude.getCheckInTime())
                .checkOutTime(savedAttitude.getCheckOutTime())
                .userId(user.getUserId())
                .userName(user.getName())
                .build();
    }

    // HTTP 요청에서 JWT 토큰을 가져와서 userId 추출
    private String getUserIdFromRequest() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String token = request.getHeader("Authorization");  // "Bearer "는 나중에 제거합니다.
        if (token != null && token.startsWith("Bearer ")) {
            token = token.replace("Bearer ", "");  // "Bearer "를 제거한 후 token만 추출
        }
        return jwtTokenProvider.getUserIdFromToken(token);  // JWT 토큰에서 userId 추출
    }
}
