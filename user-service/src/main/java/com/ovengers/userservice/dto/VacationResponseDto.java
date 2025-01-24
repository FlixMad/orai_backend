package com.ovengers.userservice.dto;

import com.ovengers.userservice.entity.VacationState;
import com.ovengers.userservice.entity.VacationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VacationResponseDto {
    private String vacationId; // 휴가 ID
    private VacationType type; // 휴가 유형
    private LocalDateTime startDate; // 시작일
    private LocalDateTime endDate; // 종료일
    private VacationState vacationState; // 휴가 상태
    private String userId; // 신청자 ID
    private String approvalId; // 결재 ID (연관된 결재)
}

