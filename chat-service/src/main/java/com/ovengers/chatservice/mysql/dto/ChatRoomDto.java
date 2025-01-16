package com.ovengers.chatservice.mysql.dto;

import lombok.*;

import java.util.List;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoomDto {

    private Long chatRoomId;

    private String image;

    private String name;

    private String creatorId;

    private String createdAt;

    private String updatedAt;

//    private List<String> userIds; // 채팅방에 참여한 사용자 ID 목록

}
