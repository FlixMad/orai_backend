package com.ovengers.chatservice.mongodb.controller;

import com.ovengers.chatservice.common.auth.TokenUserInfo;
import com.ovengers.chatservice.mongodb.dto.MessageDto;
import com.ovengers.chatservice.mongodb.dto.MessageRequestDto;
import com.ovengers.chatservice.mongodb.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@Tag(name = "MessageController", description = "단순히 MongoDB에 데이터를 저장하는 컨트롤러")
public class MessageController {
    private final MessageService messageService;
    private final SimpMessagingTemplate messagingTemplate;

//    @Operation(summary = "메시지 저장", description = "채팅방Id, 콘텐츠")
//    @PostMapping("/{chatRoomId}/saveMessage")
//    public Mono<MessageDto> saveMessage(@PathVariable Long chatRoomId,
//                                        @RequestBody MessageRequestDto messageRequestDto,
//                                        @AuthenticationPrincipal TokenUserInfo tokenUserInfo) {
//
//        return messageService.sendMessage(chatRoomId, messageRequestDto.getContent(), tokenUserInfo.getId());
////                .doOnSuccess(messageDto -> messagingTemplate.convertAndSend("/sub/" + chatRoomId + "/chat", messageDto));
//    }

    @Operation(summary = "채팅방의 메시지 조회", description = "채팅방Id")
    @GetMapping("/{chatRoomId}/messageList")
    public Flux<MessageDto> getMessages(@PathVariable Long chatRoomId,
                                        @AuthenticationPrincipal TokenUserInfo tokenUserInfo
                                        /*@RequestParam(required = false) Integer page,*/
                                        /*@RequestParam(defaultValue = "10") int size*/) {
        return messageService.getMessages(chatRoomId, tokenUserInfo.getId()/*, page, size*/);
    }

    @Operation(summary = "메시지 수정", description = "채팅방Id, 메시지Id, 콘텐츠")
    @PutMapping("/{chatRoomId}/{messageId}/updateMessage")
    public Mono<MessageDto> updateMessage(@PathVariable Long chatRoomId,
                                          @PathVariable String messageId,
                                          @RequestBody MessageRequestDto messageRequestDto,
                                          @AuthenticationPrincipal TokenUserInfo tokenUserInfo) {

        return messageService.updateMessage(chatRoomId, messageId, messageRequestDto.getContent(), tokenUserInfo.getId())
                .doOnSuccess(updatedMessage -> messagingTemplate.convertAndSend("/sub/" + chatRoomId + "/chat", updatedMessage));
    }

    @Operation(summary = "메시지 삭제", description = "채팅방Id, 메시지Id")
    @DeleteMapping("/{chatRoomId}/{messageId}/deleteMessage")
    public Mono<MessageDto> deleteMessage(@PathVariable Long chatRoomId,
                                          @PathVariable String messageId,
                                          @AuthenticationPrincipal TokenUserInfo tokenUserInfo) {
        return messageService.deleteMessage(chatRoomId, messageId, tokenUserInfo.getId())
                .doOnSuccess(deletedMessage ->
                        messagingTemplate.convertAndSend("/sub/" + chatRoomId + "/chat", deletedMessage));
    }
}
