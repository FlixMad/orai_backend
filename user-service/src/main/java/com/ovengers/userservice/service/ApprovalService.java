package com.ovengers.userservice.service;

import com.ovengers.userservice.dto.ApprovalRequestDto;
import com.ovengers.userservice.dto.ApprovalResponseDto;
import com.ovengers.userservice.entity.Approval;
import com.ovengers.userservice.entity.Vacation;
import com.ovengers.userservice.entity.VacationState;
import com.ovengers.userservice.repository.UserRepository;
import com.ovengers.userservice.repository.VacationRepository; // 추가된 부분
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@Transactional
@Service
public class ApprovalService {

    private final UserRepository userRepository;
    private final VacationRepository vacationRepository; // VacationRepository 주입

    public ApprovalResponseDto processApproval(UUID vacationId, ApprovalRequestDto requestDto) {
        Vacation vacation = vacationRepository.findById(vacationId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 휴가입니다."));
        Approval approval = vacation.getApproval();

        // 상태 업데이트
        if (requestDto.getIsApproved()) {
            approval.setStatus(VacationState.APPROVED);
            vacation.setVacationState(VacationState.APPROVED);
        } else {
            approval.setStatus(VacationState.REJECTED);
            vacation.setVacationState(VacationState.REJECTED);
        }

        approval.setUpdatedAt(LocalDateTime.now());
        vacation.setUpdatedAt(LocalDateTime.now());

        // 저장
        vacationRepository.save(vacation);

        // Response DTO 생성 및 반환
        return ApprovalResponseDto.builder()
                .approvalId(approval.getApprovalId())
                .status(approval.getStatus())
                .title(approval.getTitle())
                .contents(approval.getContents())
                .approvalUserId(approval.getApprovalUserId())
                .vacationId(vacation.getVacationId())
                .build();
    }
}
