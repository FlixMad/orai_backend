package com.ovengers.chatservice.mongodb.document;

import com.ovengers.chatservice.mongodb.dto.MessageDto;
import jakarta.persistence.*;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Document(collection = "chatMessage") // 실제 몽고 DB 컬렉션 이름
@Getter
@Setter
@NoArgsConstructor
@ToString
@AllArgsConstructor
@Builder
public class Message {
    @Id
    private ObjectId messageId;

    private String content;

    @Builder.Default
    private Long readCount = 0L;

    @CreatedDate
    private LocalDateTime createdAt;

    private Long chatRoomId;

    public MessageDto toDto() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return MessageDto.builder()
                .messageId(getMessageId())
                .content(getContent())
                .readCount(getReadCount())
                .createdAt(getCreatedAt() != null ? getCreatedAt().format(formatter) : "비어있음")
                .chatRoomId(getChatRoomId())
                .build();
    }
}
