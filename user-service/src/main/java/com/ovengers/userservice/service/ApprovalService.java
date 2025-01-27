package com.ovengers.userservice.service;

import com.ovengers.userservice.dto.ApprovalResponseDto;
import com.ovengers.userservice.entity.Approval;
import com.ovengers.userservice.repository.ApprovalRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ApprovalService {

    private final ApprovalRepository approvalRepository;

    public ApprovalService(ApprovalRepository approvalRepository) {
        this.approvalRepository = approvalRepository;
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

}
