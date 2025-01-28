package com.ovengers.chatservice.mongodb.controller;

import com.ovengers.chatservice.mongodb.dto.MessageDto;
import com.ovengers.chatservice.mongodb.service.MessageService;
import com.ovengers.chatservice.mysql.service.ChatService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

@Slf4j
@Controller
@RequiredArgsConstructor
@Tag(name = "WebSocketStompController", description = "유튜브 참고")
public class WebSocketStompController {
    private final MessageService messageService;
    private final ChatService chatService;

    /**
     * stomp 통신
     */
    @MessageMapping("/{chatRoomId}/send")
    @SendTo("/sub/{chatRoomId}/chat")
    public Mono<MessageDto> broadcastMessage(
            @DestinationVariable Long chatRoomId,
            @Payload String content,
            @Header("userId") String userId,
            @Header("userName") String userName) {

        return messageService.sendMessage(chatRoomId, content, userId, userName)
                .doOnSuccess(messageDto -> {
                    // 메시지 전송 성공 시 읽지 않은 메시지 수 증가
                    chatService.incrementUnreadCount(chatRoomId, userId);
                })
                .onErrorResume(e -> {
                    log.error("메시지 전송 실패: {}", e.getMessage());
                    // 에러 메시지도 MessageDto 형식으로 반환
                    return Mono.just(MessageDto.builder()
                            .type("ERROR")
                            .content(e.getMessage())
                            .build());
                });
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
