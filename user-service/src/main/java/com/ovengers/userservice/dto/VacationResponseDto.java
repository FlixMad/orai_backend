package com.ovengers.userservice.dto;

import com.ovengers.userservice.entity.Vacation;
import com.ovengers.userservice.entity.VacationState;
import com.ovengers.userservice.entity.VacationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VacationResponseDto {
    private String vacationId; // 휴가 ID
    private VacationType type; // 휴가 유형
    private LocalDate startDate; // 시작일
    private LocalDate endDate; // 종료일
    private VacationState vacationState; // 휴가 상태
    private String userId; // 신청자 ID
    private String userName;
    private String approvalId;// 결재 ID (연관된 결재)
    // Vacation 엔티티를 기반으로 하는 생성자 추가
    public VacationResponseDto(Vacation vacation, String userName) {
        this.vacationId = vacation.getVacationId();
        this.type = vacation.getType();
        this.startDate = vacation.getStartDate();
        this.endDate = vacation.getEndDate();
        this.vacationState = vacation.getVacationState();
        this.userId = vacation.getUserId();
        this.userName = userName;
        this.approvalId = vacation.getApproval() != null ? vacation.getApproval().getApprovalId() : null;
    }
}

