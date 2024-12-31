package com.ovengers.chatservice.mysql.dto;

import com.ovengers.chatservice.mysql.entity.ChatRoom;
import lombok.*;

import java.time.format.DateTimeFormatter;

@Getter @Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoomDto {
    private Long chatRoomId;
    private String name;
    private String createdAt;
    private String updatedAt;

    public static ChatRoomDto fromEntity(ChatRoom chatRoom) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return ChatRoomDto.builder()
                .chatRoomId(chatRoom.getChatRoomId())
                .name(chatRoom.getName())
                .createdAt(chatRoom.getCreatedAt().format(formatter))
                .updatedAt(chatRoom.getUpdatedAt().format(formatter))
                .build();
    }
}
