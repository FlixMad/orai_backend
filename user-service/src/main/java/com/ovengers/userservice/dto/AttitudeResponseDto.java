package com.ovengers.userservice.dto;

import lombok.Builder;
import lombok.Data;
import com.ovengers.userservice.entity.Attitude;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@ToString
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttitudeResponseDto {
    private String attitudeId; // 근태 ID
    private LocalDateTime createdAt; // 생성일자
    private LocalDateTime checkInTime; // 출근 시간
    private LocalDateTime checkOutTime; // 퇴근 시간
    private String userId; // 사용자 ID
    private String userName; // 사용자 이름


    public AttitudeResponseDto(Attitude attitude) {
        this.attitudeId = attitude.getAttitudeId();
        this.createdAt = attitude.getCreatedAt();
        this.checkInTime = attitude.getCheckInTime();
        this.checkOutTime = attitude.getCheckOutTime();
    }
}
