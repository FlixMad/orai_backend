package com.ovengers.chatservice.mongodb.dto;

import lombok.*;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageDto {
    private String messageId;
    private Long chatRoomId;
    private String senderId;
    private String senderName;
    private String type;
    private String content;
    private String createdAt;
    private String updatedAt;

//    private List<String> unreadUserIds; // 읽지 않은 사용자 ID 목록
}
