package com.ovengers.chatservice.mongodb.dto;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageRequestDto {
    private String content;
}
