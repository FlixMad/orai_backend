package com.ovengers.etcservice.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class NotificationMessage {
    private String type;            // "SCHEDULE"
    private String departmentId;    // "dev"
    private String scheduleId;      // "schedule123"
    private String title;           // "주간 회의"
    private String content;         // "이번 주 회의가 등록되었습니다"
    private LocalDateTime createdAt;
}