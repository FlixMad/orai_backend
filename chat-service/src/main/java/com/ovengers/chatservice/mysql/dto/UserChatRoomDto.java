    package com.ovengers.chatservice.mysql.dto;

    import lombok.*;

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public class UserChatRoomDto {
        private Long chatRoomId;
        private String userId;
    }
