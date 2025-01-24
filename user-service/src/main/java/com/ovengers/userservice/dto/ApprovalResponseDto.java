package com.ovengers.userservice.dto;

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
    private String approvalId; // 결재 ID
    private VacationState status; // 결재 상태
    private String title; // 결재 제목
    private String contents; // 결재 내용
    private String approvalUserId; // 결재자 ID
    private String vacationId; // 연관된 휴가 ID
}

