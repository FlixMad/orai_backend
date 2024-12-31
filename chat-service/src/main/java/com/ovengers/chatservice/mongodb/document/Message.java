package com.ovengers.chatservice.mongodb.document;

import com.ovengers.chatservice.mongodb.dto.MessageDto;
import jakarta.persistence.*;
import lombok.*;
import org.bson.types.ObjectId;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id")
    private ObjectId messageId;

    @Column(name = "content")
    private String content;

    @Column(name = "read_count")
    private Long readCount;

    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @JoinColumn(nullable = false)
    @Column(name = "chat_room_id")
    private Long chatRoomId;

    public MessageDto toDto() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return MessageDto.builder()
                .messageId(getMessageId())
                .content(getContent())
                .readCount(getReadCount())
                .createdAt(getCreatedAt().format(formatter))
                .chatRoomId(getChatRoomId())
                .build();
    }
}
