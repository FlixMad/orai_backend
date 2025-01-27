package com.ovengers.userservice.controllers;

import com.ovengers.userservice.dto.ApprovalResponseDto;
import com.ovengers.userservice.service.ApprovalService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
