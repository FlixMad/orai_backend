package com.ovengers.chatservice.mongodb.controller;

import com.ovengers.chatservice.mongodb.dto.MessageDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
@Tag(name = "WebSocketStompController", description = "유튜브 참고")
public class WebSocketStompController {
    /**
     * stomp 통신
     */
    @MessageMapping("/{chatRoomId}/send")
    @SendTo("/sub/{chatRoomId}/chat")
    public MessageDto broadcastMessage(
            @DestinationVariable Long chatRoomId,
            @Payload String content,
            @Header("userId") String userId,
            @Header("userName") String userName) {

        // 메시지 DTO 생성 후 브로드캐스트
        return MessageDto.builder()
                .chatRoomId(chatRoomId)
                .senderId(userId)
                .senderName(userName)
                .content(content)
                .createdAt(LocalDateTime.now().toString())
                .build();
    }

/*
    private String extractIdFromPrincipal(String principalName) {
        // 정규식으로 id 값 추출
        Pattern pattern = Pattern.compile("id=([a-f0-9-]+)");
        Matcher matcher = pattern.matcher(principalName);

        if (matcher.find()) {
            return matcher.group(1); // 첫 번째 그룹(id 값) 반환
        } else {
            throw new IllegalArgumentException("Invalid Principal format");
        }
    }

    @MessageMapping("/{chatRoomId}/messages")
    public Flux<MessageDto> getMessages(
            @DestinationVariable Long chatRoomId,
            @Header("userId") String userId) {

//        String principalId = principal.getName();
//        String userId = extractIdFromPrincipal(principalId);

        return messageService.getMessages(chatRoomId, userId);
    }

    @MessageMapping("/{chatRoomId}/{messageId}/update")
    @SendTo("/sub/{chatRoomId}/chat")
    public Mono<MessageDto> updateMessage(
            @DestinationVariable Long chatRoomId,
            @DestinationVariable String messageId,
            @Payload String newContent,
            @Header("userId") String userId) {

//        String principalId = principal.getName();
//        String senderId = extractIdFromPrincipal(principalId);

        return messageService.updateMessage(chatRoomId, messageId, newContent, userId);
    }

    @MessageMapping("/{chatRoomId}/{messageId}/delete")
    @SendTo("/sub/{chatRoomId}/chat")
    public Mono<Void> deleteMessage(
            @DestinationVariable Long chatRoomId,
            @DestinationVariable String messageId,
            @Header("userId") String userId) {

//        String principalId = principal.getName();
//        String senderId = extractIdFromPrincipal(principalId);

        return messageService.deleteMessage(chatRoomId, messageId, userId);
    }*/
}
