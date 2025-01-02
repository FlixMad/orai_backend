package com.ovengers.userservice.dto;

import com.ovengers.userservice.entity.Attitude;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttitudeResponseDto {
    private String attitudeId;
    private LocalDateTime createdAt;
    private LocalDateTime checkInTime; // 출근 시간
    private LocalDateTime checkOutTime;

    public AttitudeResponseDto(Attitude attitude) {
        this.attitudeId = attitude.getAttitudeId();
        this.createdAt = attitude.getCreatedAt();
        this.checkInTime = attitude.getCheckInTime();
        this.checkOutTime = attitude.getCheckOutTime();
    }
}
