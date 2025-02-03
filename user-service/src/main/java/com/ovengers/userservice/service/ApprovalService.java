package com.ovengers.userservice.service;

import com.ovengers.userservice.dto.ApprovalRequestDto;
import com.ovengers.userservice.dto.ApprovalResponseDto;
import com.ovengers.userservice.entity.Approval;
import com.ovengers.userservice.entity.Vacation;
import com.ovengers.userservice.entity.VacationState;
import com.ovengers.userservice.repository.ApprovalRepository;
import com.ovengers.userservice.repository.VacationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ApprovalService {

    private final ApprovalRepository approvalRepository;
    private final VacationRepository vacationRepository; // vacationRepository 필드 추가

    // 생성자에서 vacationRepository도 주입
    public ApprovalService(ApprovalRepository approvalRepository, VacationRepository vacationRepository) {
        this.approvalRepository = approvalRepository;
        this.vacationRepository = vacationRepository; // vacationRepository 주입
    }

    // 특정 approvalUserId로 승인 목록 조회
    public List<ApprovalResponseDto> getApprovalsByUserId(String approvalUserId) {
        List<Approval> approvals = approvalRepository.findAllByApprovalUserId(approvalUserId);

        // 데이터가 없으면 예외를 던짐
        if (approvals.isEmpty()) {
            throw new IllegalArgumentException("No approvals found for user: " + approvalUserId);
        }

        return approvals.stream()
                .map(approval -> new ApprovalResponseDto(approval)) // 생성자 호출
                .collect(Collectors.toList());
    }

    public ApprovalResponseDto processApproval(String vacationId, ApprovalRequestDto requestDto, String state) {
        // ✅ 해당 휴가의 Vacation을 찾아 상태를 업데이트
        Vacation vacation = vacationRepository.findById(vacationId)
                .orElseThrow(() -> new IllegalArgumentException("Vacation not found"));

        // ✅ 기존 Approval 엔티티 조회
        Approval approval = approvalRepository.findByVacation_VacationId(vacationId);
        if (approval == null) {
            throw new IllegalArgumentException("Approval record not found for vacationId: " + vacationId);
        }

        // ✅ 승인/거절 상태 업데이트 (Vacation & Approval 둘 다 변경!)
        VacationState vacationState = VacationState.valueOf(state);
        vacation.setVacationState(vacationState);
        vacationRepository.save(vacation); // 🔹 Vacation 변경 저장

        approval.setVacationState(vacationState); // 🔹 Approval 상태도 업데이트
        approvalRepository.save(approval); // 🔹 Approval 변경 저장

        // ✅ ApprovalResponseDto 생성
        return ApprovalResponseDto.builder()
                .approvalId(approval.getApprovalId())
                .title(requestDto.getTitle())
                .contents(requestDto.getContents())
                .vacationState(vacation.getVacationState())
                .vacationId(vacation.getVacationId())
                .approvalUserId(approval.getApprovalUserId()) // ✅ Approval의 승인자 ID 사용
                .build();
    }


}
