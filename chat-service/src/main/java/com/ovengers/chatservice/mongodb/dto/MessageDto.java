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
    private String senderImage;
    private String senderName;
    private String type;
    private String content;
    private String createdAt;
    private String updatedAt;
}
