package com.ovengers.userservice.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
@Builder
@Data
public class AttitudeResponseDto {
    private String attitudeId; // 근태 ID
    private LocalDateTime createdAt; // 생성일자
    private LocalDateTime checkInTime; // 출근 시간
    private LocalDateTime checkOutTime; // 퇴근 시간
    private String userId; // 사용자 ID
    private String userName; // 사용자 이름
}
