package com.ovengers.chatservice.mysql.dto;

import com.ovengers.chatservice.mysql.entity.ChatRoom;
import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoomDto {
    private Long chatRoomId;
    private String name;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ChatRoomDto fromEntity(ChatRoom chatRoom) {
        return ChatRoomDto.builder()
                .chatRoomId(chatRoom.getChatRoomId())
                .name(chatRoom.getName())
                .createdAt(chatRoom.getCreatedAt())
                .updatedAt(chatRoom.getUpdatedAt())
                .build();
    }
}
