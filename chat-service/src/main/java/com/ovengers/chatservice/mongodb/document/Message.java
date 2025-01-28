package com.ovengers.chatservice.mongodb.document;

import com.ovengers.chatservice.mongodb.dto.MessageDto;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Document(collection = "message")
@Getter
@NoArgsConstructor
@ToString
@AllArgsConstructor
@Builder
public class Message {
    @Id
    private String messageId;

    @Setter
    private Long chatRoomId;

    @Setter
    private String senderId;

    @Setter
    private String senderName;

    @Setter
    private String type = "CHAT"; // CHAT, SYSTEM, EDIT, DELETE, ERROR 등

    @Setter
    private String content;

    @CreatedDate
    @Field("created_at")
    private LocalDateTime createdAt;

    @Setter
    @LastModifiedDate
    @Field("updated_at")
    private LocalDateTime updatedAt;

    public MessageDto toDto() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return MessageDto.builder()
                .messageId(messageId)
                .chatRoomId(chatRoomId)
                .senderId(senderId)
                .senderName(senderName)
                .type(type)
                .content(content)
                .createdAt(createdAt != null ? createdAt.format(formatter) : "메시지생성시간없음")
                .updatedAt(updatedAt != null ? updatedAt.format(formatter) : "메시지수정시간없음")
                .build();
    }
}
