package com.ovengers.chatservice.mongodb.controller;

import com.ovengers.chatservice.common.auth.TokenUserInfo;
import com.ovengers.chatservice.mongodb.document.Message;
import com.ovengers.chatservice.mongodb.dto.MessageDto;
import com.ovengers.chatservice.mongodb.service.MessageService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@Tag(name = "MessageController", description = "단순히 MongoDB에 데이터를 저장하는 컨트롤러")
public class MessageController {
    private final MessageService messageService;

    public String cleanInput(String input) {
        if (input == null) {
            return null;
        }
        // 문자열 양 끝의 쌍따옴표만 제거
        return input.startsWith("\"") && input.endsWith("\"")
                ? input.substring(1, input.length() - 1)
                : input;
    }

    /**
     * 데이터 저장
     */
    @PostMapping("/{chatRoomId}/createMessage")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<MessageDto> createMessage(@PathVariable Long chatRoomId, @RequestBody String content, @AuthenticationPrincipal TokenUserInfo tokenUserInfo) {

        String cleanedContent = cleanInput(content);

        try {
            // content만 받아서 Message 객체 생성
            Message message = new Message();
            message.setChatRoomId(chatRoomId); // 채팅방 ID 설정
            message.setContent(cleanedContent); // 클라이언트가 보낸 content 설정
            message.setSenderId(tokenUserInfo.getId());

            return messageService.createMessage(message);
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }

/*
     // 데이터 조회
    @GetMapping("/{chatRoomId}/getMessages")
    public Mono<ResponseEntity<List<MessageDto>>> getMessages(@PathVariable Long chatRoomId) {
        try {
            return messageService.getMessages(chatRoomId)
                    .collectList()
                    .map(ResponseEntity::ok);
        } catch (IllegalArgumentException ex) {
            return Mono.just(ResponseEntity.badRequest().body(null));
        }
    }

    // 데이터 수정
    @PutMapping("/{messageId}/updateMessage")
    public Mono<ResponseEntity<MessageDto>> updateMessage(
            @PathVariable String messageId,
            @RequestBody MessageRequestDto requestDto) {
        return messageService.updateMessage(messageId, requestDto.getContent())
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    // 데이터 삭제
    @DeleteMapping("/{messageId}/deleteMessage")
    public Mono<ResponseEntity<Void>> deleteMessage(@PathVariable String messageId) {
        return messageService.deleteMessage(messageId)
                .then(Mono.just(ResponseEntity.ok().<Void>build()))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }*/

}
