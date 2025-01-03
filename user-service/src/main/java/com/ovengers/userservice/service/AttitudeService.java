package com.ovengers.userservice.service;

import com.ovengers.userservice.dto.AttitudeResponseDto;
import com.ovengers.userservice.entity.Attitude;
import com.ovengers.userservice.entity.User;
import com.ovengers.userservice.repository.AttitudeRepository;
import com.ovengers.userservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AttitudeService {

    private final AttitudeRepository attitudeRepository;
    private final UserRepository userRepository;

    @Autowired
    public AttitudeService(AttitudeRepository attitudeRepository, UserRepository userRepository) {
        this.attitudeRepository = attitudeRepository;
        this.userRepository = userRepository;
    }

    // 출근 기록
    public AttitudeResponseDto recordCheckIn(String userId) {
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
    public AttitudeResponseDto recordCheckOut(String userId) {
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
}
