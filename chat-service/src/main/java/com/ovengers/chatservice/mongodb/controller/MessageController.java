package com.ovengers.chatservice.mongodb.controller;

import com.ovengers.chatservice.mongodb.dto.MessageDto;
import com.ovengers.chatservice.mongodb.service.MessageService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "MessageController", description = "메시지 관련 controller")
public class MessageController {
    private final MessageService messageService;
    private final SimpMessageSendingOperations template;

    @PostMapping("/send")
    public MessageDto sendMessage(@RequestBody MessageDto messageDto) {
        return messageService.saveChat(messageDto);
    }

    // 채팅방마다의 전체 메시지
    @GetMapping("/{chatRoomId}/find")
    public Mono<ResponseEntity<List<MessageDto>>> find(@PathVariable Long chatRoomId) {
        Flux<MessageDto> response = messageService.findMessages(chatRoomId);
        return response.collectList().map(ResponseEntity::ok);
    }

}
