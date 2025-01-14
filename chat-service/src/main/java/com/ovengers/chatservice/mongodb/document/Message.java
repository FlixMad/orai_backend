package com.ovengers.chatservice.mongodb.document;

import com.ovengers.chatservice.mongodb.dto.MessageDto;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "message")
@Getter
@NoArgsConstructor
@ToString
@AllArgsConstructor
@Builder
public class Message {
    @Id
    private String messageId;

    private String content;

    @CreatedDate
    @Field("created_at")
    private LocalDateTime createdAt;

    private Long chatRoomId;

    private String senderId;

    @Field("unread_user_ids")
    private List<String> unreadUserIds = new ArrayList<>();

    public MessageDto toDto() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return MessageDto.builder()
                .messageId(messageId)
                .content(content)
                .createdAt(createdAt != null ? createdAt.format(formatter) : "메시지생성시간없음")
                .chatRoomId(chatRoomId)
                .senderId(senderId)
                .unreadUserIds(unreadUserIds)
                .build();
    }

    public void setChatRoomId(Long chatRoomId) {
        this.chatRoomId = chatRoomId;
    }

    public void setContent(String cleanedContent) {
        this.content = cleanedContent;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }
}
