package com.ovengers.chatservice.mysql.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomRequestDto {
    private String image;
    private String name;
//    private List<String> inviteUserIds; // 초대할 사용자 ID 리스트
}