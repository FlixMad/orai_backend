package com.ovengers.chatservice.mongodb.controller;

import com.ovengers.chatservice.mongodb.document.Message;
import com.ovengers.chatservice.mongodb.service.MessageService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@Tag(name = "ReactiveMongoDBMessageController", description = "리액티브 MongoDB 메시지 관련 controller")
public class WebSocketController {
    private final MessageService messageService;

    @GetMapping("/{chatRoomId}/getMessages")
    public Flux<Message> getMessageByChatRoomId(@PathVariable Long chatRoomId) {
        return messageService.getMessageByChatRoomId(chatRoomId);
    }

    @PostMapping("/createMessage")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Message> createMessage(@RequestBody Message message) {
        return messageService.createMessage(message);
    }

    @PutMapping("/{messageId}/updateMessage")
    public Mono<ResponseEntity<Message>> updateUser(@PathVariable String  messageId, @RequestBody Message message) {
        return messageService.updateMessage(messageId, message)
                .map(updatedMessage -> ResponseEntity.ok(updatedMessage))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{messageId}/deleteMessage")
    public Mono<ResponseEntity<Void>> deleteUser(@PathVariable String messageId) {
        return messageService.deleteMessage(messageId)
                .then(Mono.just(ResponseEntity.ok().<Void>build()))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
