package com.ovengers.chatservice.mongodb.controller;

import com.ovengers.chatservice.mongodb.dto.MessageDto;
import com.ovengers.chatservice.mongodb.service.MessageService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@Tag(name = "WebSocketStompController", description = "유튜브 참고")
public class WebSocketStompController {
    private final MessageService messageService;

//    private String extractIdFromPrincipal(String principalName) {
//        // 정규식으로 id 값 추출
//        Pattern pattern = Pattern.compile("id=([a-f0-9-]+)");
//        Matcher matcher = pattern.matcher(principalName);
//
//        if (matcher.find()) {
//            return matcher.group(1); // 첫 번째 그룹(id 값) 반환
//        } else {
//            throw new IllegalArgumentException("Invalid Principal format");
//        }
//    }

    /**
     * 메시지 전송
     */
    @MessageMapping("/{chatRoomId}/send")
    @SendTo("/sub/{chatRoomId}/chat")
    public Mono<MessageDto> sendMessage(
            @DestinationVariable Long chatRoomId,
            @Payload String content,
            @Header("userId") String userId) {

//        String principalId = principal.getName();
//        String senderId = extractIdFromPrincipal(principalId);

        return messageService.sendMessage(chatRoomId, content, userId);
    }

    /**
     * 메시지 조회
     */
    @MessageMapping("/{chatRoomId}/messages")
    public Flux<MessageDto> getMessages(
            @DestinationVariable Long chatRoomId,
            @Header("userId") String userId) {

//        String principalId = principal.getName();
//        String userId = extractIdFromPrincipal(principalId);

        return messageService.getMessages(chatRoomId, userId);
    }

    /**
     * 메시지 수정
     */
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

    /**
     * 메시지 삭제
     */
    @MessageMapping("/{chatRoomId}/{messageId}/delete")
    @SendTo("/sub/{chatRoomId}/chat")
    public Mono<Void> deleteMessage(
            @DestinationVariable Long chatRoomId,
            @DestinationVariable String messageId,
            @Header("userId") String userId) {

//        String principalId = principal.getName();
//        String senderId = extractIdFromPrincipal(principalId);

        return messageService.deleteMessage(chatRoomId, messageId, userId);
    }
}
