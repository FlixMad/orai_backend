package com.ovengers.etcservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;


@Getter @Setter @ToString
@NoArgsConstructor // 기본 생성자 추가
@AllArgsConstructor
@Builder
public class NotificationMessage {
    private String type;
    private String departmentId;
    private String scheduleId;
    private String title;
    private String content;
    private LocalDateTime createdAt;
}
