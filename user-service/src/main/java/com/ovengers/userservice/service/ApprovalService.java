package com.ovengers.userservice.service;

import com.ovengers.userservice.dto.ApprovalRequestDto;
import com.ovengers.userservice.dto.ApprovalResponseDto;
import com.ovengers.userservice.entity.Approval;
import com.ovengers.userservice.entity.Vacation;
import com.ovengers.userservice.entity.VacationState;
import com.ovengers.userservice.entity.User;
import com.ovengers.userservice.repository.ApprovalRepository;
import com.ovengers.userservice.repository.UserRepository;
import com.ovengers.userservice.repository.VacationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ApprovalService {

    private final ApprovalRepository approvalRepository;
    private final VacationRepository vacationRepository;
    private final UserRepository userRepository; // ✅ UserRepository 추가

    // ✅ 생성자에서 userRepository 주입 추가
    public ApprovalService(ApprovalRepository approvalRepository, VacationRepository vacationRepository, UserRepository userRepository) {
        this.approvalRepository = approvalRepository;
        this.vacationRepository = vacationRepository;
        this.userRepository = userRepository;
    }

    // ✅ 특정 approvalUserId로 승인 목록 조회
    public List<ApprovalResponseDto> getApprovalsByUserId(String approvalUserId) {
        List<Approval> approvals = approvalRepository.findAllByApprovalUserId(approvalUserId);

        if (approvals.isEmpty()) {
            throw new IllegalArgumentException("No approvals found for user: " + approvalUserId);
        }

        return approvals.stream().map(approval -> {
            String requestUserId = approval.getVacation().getUserId();
            String requestUserName = userRepository.findById(requestUserId)
                    .map(User::getName)
                    .orElse("Unknown User");

            return new ApprovalResponseDto(approval, requestUserName); // 생성자 호출 시 이름 전달
        }).collect(Collectors.toList());
    }


    // ✅ 승인/거절 처리 메서드
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

        // ✅ 신청자 ID를 이용해 이름 조회
        String requestUserName = userRepository.findById(vacation.getUserId())
                .map(User::getName)
                .orElse("Unknown User"); // 사용자가 없으면 기본값 설정

        // ✅ ApprovalResponseDto 생성
        return ApprovalResponseDto.builder()
                .approvalId(approval.getApprovalId())
                .title(vacation.getTitle()) // ✅ title 추가
                .contents(approval.getContents()) // ✅ 결재 내용 유지
                .vacationState(vacation.getVacationState())
                .vacationId(vacation.getVacationId())
                .approvalUserId(approval.getApprovalUserId())
                .requestUserId(vacation.getUserId()) // ✅ 신청자 ID 추가
                .requestUserName(requestUserName) // ✅ 신청자 이름 추가
                .startDate(vacation.getStartDate()) // ✅ 시작일 추가
                .endDate(vacation.getEndDate()) // ✅ 종료일 추가
                .build();
    }
}
