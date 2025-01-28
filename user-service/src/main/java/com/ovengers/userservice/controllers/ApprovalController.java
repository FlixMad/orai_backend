package com.ovengers.userservice.controllers;

import com.ovengers.userservice.dto.ApprovalRequestDto;
import com.ovengers.userservice.dto.ApprovalResponseDto;
import com.ovengers.userservice.service.ApprovalService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/approvals")
public class ApprovalController {

    private final ApprovalService approvalService;

    public ApprovalController(ApprovalService approvalService) {
        this.approvalService = approvalService;
    }

    // approvalUserId로 승인 목록 조회
    @GetMapping("/user/{approvalUserId}")
    public ResponseEntity<List<ApprovalResponseDto>> getApprovalsByUserId(@PathVariable String approvalUserId) {
        List<ApprovalResponseDto> responseDtos = approvalService.getApprovalsByUserId(approvalUserId);
        return ResponseEntity.ok(responseDtos); // 데이터가 있을 경우 200 반환
    }

    // 승인 처리 (승인 또는 거절)
    @PostMapping("/approve/{vacationId}")
    public ResponseEntity<ApprovalResponseDto> approveVacation(
            @PathVariable String vacationId,
            @RequestBody ApprovalRequestDto requestDto) {

        ApprovalResponseDto responseDto = approvalService.processApproval(vacationId, requestDto, "APPROVED");
        return ResponseEntity.ok(responseDto); // 승인 처리 후 응답 반환
    }

    // 거절 처리
    @PostMapping("/reject/{vacationId}")
    public ResponseEntity<ApprovalResponseDto> rejectVacation(
            @PathVariable String vacationId,
            @RequestBody ApprovalRequestDto requestDto) {

        ApprovalResponseDto responseDto = approvalService.processApproval(vacationId, requestDto, "REJECTED");
        return ResponseEntity.ok(responseDto); // 거절 처리 후 응답 반환
    }
}
