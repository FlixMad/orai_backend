package com.ovengers.chatservice.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoomDto {
    private String name;
    private LocalDateTime createdAt;
}
