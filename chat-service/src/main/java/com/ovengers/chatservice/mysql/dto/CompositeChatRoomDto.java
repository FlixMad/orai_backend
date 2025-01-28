package com.ovengers.chatservice.mysql.dto;

import lombok.*;

import java.util.List;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompositeChatRoomDto {
    private ChatRoomDto chatRoomDto;
    private List<UserChatRoomDto> userChatRoomDto; // 초대된 유저 목록
}
