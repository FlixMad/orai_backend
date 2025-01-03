package com.ovengers.chatservice.mongodb.controller;

import com.ovengers.chatservice.mongodb.document.Message;
import com.ovengers.chatservice.mongodb.dto.MessageDto;
import com.ovengers.chatservice.mongodb.service.WebSocketStompService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@Tag(name = "WebSocketStompController", description = "웹소켓 STOMP 관련 controller")
public class WebSocketStompController {

    private final WebSocketStompService webSocketStompService;


    /**
     * 메시지 송신 (STOMP 기반)
     */
    @MessageMapping("/send")
    @SendTo("/sub/chat")
    public Mono<MessageDto> sendMessage(/*@PathVariable Long chatRoomId,*/@RequestBody Message message) {
        return webSocketStompService.sendMessage(message);
    }

    /**
     * 메시지 수정
     */
    @PutMapping("/{messageId}/update")
    public Mono<MessageDto> updateMessage(@PathVariable String messageId, @RequestBody String newContent) {
        return webSocketStompService.updateMessage(messageId, newContent);
    }

    /**
     * 메시지 삭제
     */
    @DeleteMapping("/{messageId}")
    public Mono<Void> deleteMessage(@PathVariable String messageId) {
        return webSocketStompService.deleteMessage(messageId);
    }

    /**
     * 특정 채팅방의 메시지 조회
     */
    @GetMapping("/{chatRoomId}/messages")
    public Flux<MessageDto> getMessages(@PathVariable Long chatRoomId) {
        return webSocketStompService.getMessagesByChatRoom(chatRoomId);
    }
}