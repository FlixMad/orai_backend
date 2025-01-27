package com.ovengers.userservice.dto;

import com.ovengers.userservice.entity.VacationState;
import lombok.*;

import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalRequestDto {

    @NotNull
    private VacationState approvalState;

}
