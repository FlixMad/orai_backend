package com.ovengers.chatservice.mysql.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UnreadMessageDto {
    private Long unreadCount; // 읽지 않은 메시지 수
    private Long chatRoomId; // 채팅방 ID
    private String userId; // 사용자 ID
}
