package com.ovengers.chatservice.mysql.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoomUnreadDto {
    private Long chatRoomId;
    private String creatorId;
    private String name;
    private String image;
    private Long unreadCount;
    private String lastMessage;
    private String lastMessageTime;
}