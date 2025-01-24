package com.ovengers.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotNull;
@NotNull
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalRequestDto {
    @NotNull
    private Boolean isApproved; // 승인 여부
}
