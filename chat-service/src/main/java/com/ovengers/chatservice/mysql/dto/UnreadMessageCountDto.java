package com.ovengers.chatservice.mysql.dto;

import com.ovengers.chatservice.mysql.entity.ChatRoomRead;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UnreadMessageCountDto {
    private Long chatRoomId;
    private Long unreadCount;
    private String lastReadMessageId;
    private String lastReadAt;

    public static UnreadMessageCountDto from(ChatRoomRead entity) {
        return UnreadMessageCountDto.builder()
                .chatRoomId(entity.getChatRoomId())
                .unreadCount(entity.getUnreadCount())
                .lastReadMessageId(entity.getLastReadMessageId())
                .lastReadAt(entity.getLastReadAt() != null
                        ? entity.getLastReadAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                        : null)
                .build();
    }
}