package com.ovengers.chatservice.mysql.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoomNotificationDto {
    private Long chatRoomId;
    private String name;
    private String message;
}
