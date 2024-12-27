package com.ovengers.chatservice.dto;

import lombok.*;

@Getter @ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageDto {
/*    // 메시지  타입 : 입장, 채팅
    public enum MessageType{
        JOIN, TALK, LEAVE
    }

    private MessageType messageType; // 메시지 타입
    private Long chatRoomId; // 방 번호
    private String sender; // 메시지 보낸 사람
    private String message; // 메시지*/

    private Long id; // 메시지 아이디
    private String name; //
    private String message;

}
