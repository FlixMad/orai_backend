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
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "MessageController", description = "단순히 MongoDB에 CRUD하는 컨트롤러")
public class MessageController {
    private final MessageService messageService;

    /**
     * 데이터 조회
     */
    @GetMapping("/{chatRoomId}/getMessages")
    public Mono<ResponseEntity<List<MessageDto>>> getMessages(@PathVariable Long chatRoomId) {
        return messageService.getMessages(chatRoomId)
                .collectList()
                .map(ResponseEntity::ok);
    }

    /**
     * 데이터 저장
     */
    @PostMapping("/createMessage")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<MessageDto> createMessage(@RequestBody Message message) {
        return messageService.createMessage(message);
    }

    /**
     * 데이터 수정
     */
    @PutMapping("/{messageId}/updateMessage")
    public Mono<ResponseEntity<MessageDto>> updateUser(
            @PathVariable String messageId,
            @RequestBody MessageRequestDto requestDto) {
        return messageService.updateMessage(messageId, requestDto.getContent())
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * 데이터 삭제
     */
    @DeleteMapping("/{messageId}/deleteMessage")
    public Mono<ResponseEntity<Void>> deleteUser(@PathVariable String messageId) {
        return messageService.deleteMessage(messageId)
                .then(Mono.just(ResponseEntity.ok().<Void>build()))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
