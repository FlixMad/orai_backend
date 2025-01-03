package com.ovengers.chatservice.mongodb.controller;

import com.ovengers.chatservice.mongodb.document.Message;
import com.ovengers.chatservice.mongodb.dto.MessageDto;
import com.ovengers.chatservice.mongodb.dto.MessageRequestDto;
import com.ovengers.chatservice.mongodb.service.MessageService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "MessageController", description = "메시지 관련 controller")
public class MessageController {
    private final MessageService messageService;

    @GetMapping("/{chatRoomId}/find")
    public Mono<ResponseEntity<List<MessageDto>>> find(@PathVariable Long chatRoomId) {
        Flux<MessageDto> response = messageService.findMessages(chatRoomId);
        return response.collectList().map(ResponseEntity::ok);
    }

    // 채팅방마다의 전체 메시지
    @GetMapping("/{chatRoomId}/getMessages")
    public Flux<MessageDto> getMessages(@PathVariable Long chatRoomId) {
        return messageService.getMessages(chatRoomId);
    }

    // 메시지 전송
    @PostMapping("/createMessage")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Message> createMessage(@RequestBody Message message) {
        return messageService.createMessage(message);
    }

    // 메시지 수정
    @PutMapping("/{messageId}/updateMessage")
    public Mono<ResponseEntity<Message>> updateUser(@PathVariable String  messageId, @RequestBody MessageRequestDto requestDto) {
        return messageService.updateMessage(messageId, requestDto.getContent())
                .map(updatedMessage -> ResponseEntity.ok(updatedMessage))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    // 메시지 삭제
    @DeleteMapping("/{messageId}/deleteMessage")
    public Mono<ResponseEntity<Void>> deleteUser(@PathVariable String messageId) {
        return messageService.deleteMessage(messageId)
                .then(Mono.just(ResponseEntity.ok().<Void>build()))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
