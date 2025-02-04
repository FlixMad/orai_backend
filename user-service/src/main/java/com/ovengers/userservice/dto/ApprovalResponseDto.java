package com.ovengers.userservice.dto;

import com.ovengers.userservice.entity.Approval;
import com.ovengers.userservice.entity.VacationState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApprovalResponseDto {
    private String approvalId;
    private String title; // ✅ 휴가 제목 추가 (Vacation의 title 사용)
    private String contents; // 결재 내용
    private String vacationId;
    private String approvalUserId;
    private String requestUserName; // ✅ 신청자 이름 추가
    private String requestUserId; // 휴가 신청자 ID
    private VacationState vacationState;
    private LocalDate startDate; // ✅ 휴가 시작일 추가
    private LocalDate endDate;   // ✅ 휴가 종료일 추가

    public ApprovalResponseDto(Approval approval, String requestUserName) {
        this.approvalId = approval.getApprovalId();
        this.vacationId = approval.getVacationId();
        this.approvalUserId = approval.getApprovalUserId();
        this.requestUserId = approval.getVacation().getUserId(); // ✅ 신청자 ID 추가
        this.requestUserName = requestUserName; // ✅ 신청자 이름 추가
        this.title = approval.getVacation().getTitle(); // ✅ Vacation의 title 사용
        this.contents = approval.getContents(); // 결재 내용 유지
        this.vacationState = approval.getVacationState();
        this.startDate = approval.getVacation().getStartDate(); // ✅ 시작일 추가
        this.endDate = approval.getVacation().getEndDate(); // ✅ 종료일 추가
    }
}
