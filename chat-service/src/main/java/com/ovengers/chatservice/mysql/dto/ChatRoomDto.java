package com.ovengers.chatservice.mysql.dto;

import lombok.*;

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
}
