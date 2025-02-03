package com.ovengers.userservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ovengers.userservice.entity.VacationType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime; // LocalDateTime import


@Data
@NoArgsConstructor
@AllArgsConstructor
public class VacationRequestDto {
    @NotNull
    private VacationType type; // 휴가 유형

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate; // 시작일

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate; // 종료일

    @NotNull
    private String userId; // 신청자 ID
    @NotNull
    private String title;
}

