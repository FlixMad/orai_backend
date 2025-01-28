package com.ovengers.userservice.dto;

import com.ovengers.userservice.entity.VacationType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime; // LocalDateTime import


@Data
@NoArgsConstructor
@AllArgsConstructor
public class VacationRequestDto {
    @NotNull
    private VacationType type; // 휴가 유형

    @NotNull
    private LocalDateTime startDate; // 시작일

    @NotNull
    private LocalDateTime endDate; // 종료일

    @NotNull
    private String userId; // 신청자 ID
    @NotNull
    private String title;
}

