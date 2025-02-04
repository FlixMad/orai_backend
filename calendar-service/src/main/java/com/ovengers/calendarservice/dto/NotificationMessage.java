package com.ovengers.calendarservice.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Data
@Builder
public class NotificationMessage {
    private String type;            // "SCHEDULE"
    private String departmentId;    // "dev"
    private String scheduleId;      // "schedule123"
    private String title;           // "주간 회의"
    private String content;         // "이번 주 회의가 등록되었습니다"

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")  // LocalDateTime을 JSON으로 변환 시 이 형식으로 처리
    private LocalDateTime createdAt;

    @JsonCreator
    public NotificationMessage(
            @JsonProperty("type") String type,
            @JsonProperty("departmentId") String departmentId,
            @JsonProperty("scheduleId") String scheduleId,
            @JsonProperty("title") String title,
            @JsonProperty("content") String content,
            @JsonProperty("createdAt") LocalDateTime createdAt
    ) {
        this.type = type;
        this.departmentId = departmentId;
        this.scheduleId = scheduleId;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
    }
}
