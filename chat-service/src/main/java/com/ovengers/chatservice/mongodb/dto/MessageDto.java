package com.ovengers.chatservice.mongodb.dto;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageDto {
    private String messageId;
    private String content;
    private String createdAt;
    private Long chatRoomId;
    private String senderId;
}
