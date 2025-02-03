package com.ovengers.userservice.dto;

import com.ovengers.userservice.entity.VacationState;
import lombok.*;

import jakarta.validation.constraints.NotNull;

@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalRequestDto {

    @NotNull
    private VacationState approvalState;
    private String approvalId;  // 승인 ID
    private String title;       // 제목
    private String contents;    // 내용
    private String approvalUserId;  // 결재자 ID 필드 추가
}
