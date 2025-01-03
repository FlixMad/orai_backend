package com.ovengers.userservice.dto;

import lombok.Builder;
import lombok.Data;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Builder
@Data
public class AttitudeRequestDto {
    @NotNull
    private LocalDateTime checkInTime; // 출근 시간

    @NotNull
    private LocalDateTime checkOutTime; // 퇴근 시간

    @NotNull
    private String userId; // 사용자 ID
}
