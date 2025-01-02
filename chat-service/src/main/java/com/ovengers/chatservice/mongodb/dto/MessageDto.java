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
    private Long readCount;
    private String createdAt;
    private Long chatRoomId;
    private Long userId;
}
