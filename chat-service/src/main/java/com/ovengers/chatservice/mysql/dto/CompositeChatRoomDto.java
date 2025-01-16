package com.ovengers.chatservice.mysql.dto;

import lombok.*;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompositeChatRoomDto {
    private ChatRoomDto chatRoomDto;
    private UserChatRoomDto userChatRoomDto;
}
