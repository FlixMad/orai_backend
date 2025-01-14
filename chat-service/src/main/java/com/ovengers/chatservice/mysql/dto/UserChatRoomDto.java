    package com.ovengers.chatservice.mysql.dto;

    import lombok.*;

    @Getter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public class UserChatRoomDto {
        private Long id;
        private String subAt;
        private Long chatRoomId;
        private String userId;
    }
