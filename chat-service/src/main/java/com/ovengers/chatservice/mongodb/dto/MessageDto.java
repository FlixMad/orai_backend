package com.ovengers.chatservice.mongodb.dto;

import com.ovengers.chatservice.mongodb.document.Message;
import lombok.*;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;

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
    private String createdAt;
    private Long chatRoomId;
}
