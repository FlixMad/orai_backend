package com.ovengers.userservice.controllers;

import com.ovengers.userservice.dto.ApprovalRequestDto;
import com.ovengers.userservice.dto.ApprovalResponseDto;
import com.ovengers.userservice.service.ApprovalService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@AllArgsConstructor
@RestController
@RequestMapping("/api/approvals")
public class ApprovalController {

    private final ApprovalService approvalService;

    @PostMapping("/{vacationId}/process")
    public ResponseEntity<ApprovalResponseDto> processApproval(@PathVariable UUID vacationId, // UUID로 변경
                                                               @RequestBody ApprovalRequestDto requestDto) {
        ApprovalResponseDto responseDto = approvalService.processApproval(vacationId, requestDto);
        return ResponseEntity.ok(responseDto);
    }
}
