package com.ovengers.chatservice.dto;

import com.ovengers.chatservice.entity.Message;
import lombok.*;

import java.time.LocalDateTime;

@Getter @ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageDto {

    private String messageId;
    private String content;
    private Long readCount;
    private LocalDateTime createdAt;
    private Long chatRoomId;

    public static MessageDto fromEntity(Message message) {
        return MessageDto.builder()
                .messageId(message.getMessageId())
                .content(message.getContent())
                .readCount(message.getReadCount())
                .createdAt(message.getCreatedAt())
                .chatRoomId(message.getChatRoomId())
                .build();
    }

}
