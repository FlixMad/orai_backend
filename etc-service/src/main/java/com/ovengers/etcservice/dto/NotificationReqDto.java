package com.ovengers.etcservice.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationReqDto {

    private String title; // 알림 제목
    private String message; // 알림 메시지
    private String userId; // 대상 사용자 ID

}
