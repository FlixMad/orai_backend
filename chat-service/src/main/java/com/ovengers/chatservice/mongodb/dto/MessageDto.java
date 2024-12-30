package com.ovengers.chatservice.mongodb.dto;

import com.ovengers.chatservice.mongodb.entity.Message;
import com.ovengers.chatservice.mysql.dto.ChatRoomDto;
import com.ovengers.chatservice.mysql.entity.ChatRoom;
import jakarta.persistence.*;
import lombok.*;
import org.bson.types.ObjectId;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageDto {
    private ObjectId messageId;
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
