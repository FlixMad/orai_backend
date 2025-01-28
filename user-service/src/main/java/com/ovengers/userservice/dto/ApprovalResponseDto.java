package com.ovengers.userservice.dto;

import com.ovengers.userservice.entity.Approval;
import com.ovengers.userservice.entity.VacationState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApprovalResponseDto {
    private String approvalId;       // 승인 ID
    private String title;            // 승인 제목
    private String contents;         // 승인 내용
    private VacationState vacationState; // 승인 상태
    private String vacationId;       // 연관된 휴가 ID
    private String approvalUserId;   // 승인자 ID
    // Approval 엔티티를 기반으로 하는 생성자 추가
    public ApprovalResponseDto(Approval approval) {
        this.approvalId = approval.getApprovalId();
        this.title = approval.getTitle();
        this.contents = approval.getContents();
        this.vacationState = approval.getVacationState();
        this.vacationId = approval.getVacation() != null ? approval.getVacation().getVacationId() : null;
        this.approvalUserId = approval.getApprovalUserId();
    }
}
