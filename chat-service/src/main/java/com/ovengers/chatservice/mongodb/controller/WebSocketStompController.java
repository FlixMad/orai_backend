package com.ovengers.chatservice.mongodb.controller;

import com.ovengers.chatservice.common.auth.TokenUserInfo;
import com.ovengers.chatservice.common.dto.CommonResDto;
import com.ovengers.chatservice.mongodb.document.Message;
import com.ovengers.chatservice.mongodb.dto.MessageDto;
import com.ovengers.chatservice.mongodb.service.WebSocketStompService;
import com.ovengers.chatservice.mysql.dto.UserChatRoomDto;
import com.ovengers.chatservice.mysql.service.UserChatRoomService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@Tag(name = "WebSocketStompController", description = "웹소켓 STOMP 관련 controller")
public class WebSocketStompController {

    private final WebSocketStompService webSocketStompService;
    private final UserChatRoomService userChatRoomService;

    /**
     * 메시지 송신 (STOMP 기반)
     */
    @MessageMapping("/{chatRoomId}/send")
    @SendTo("/sub/{chatRoomId}/chat")
    public Mono<MessageDto> sendMessage(@DestinationVariable Long chatRoomId,
                                        @RequestBody String content,
                                        @AuthenticationPrincipal TokenUserInfo tokenUserInfo) {
        try {
            Message message = new Message();
            message.setContent(content);
            message.setChatRoomId(chatRoomId);
            message.setSenderId(tokenUserInfo.getId());

            // 채팅방 구독 정보 저장
            userChatRoomService.subscribeToChatRoom(
                    UserChatRoomDto.builder()
                            .chatRoomId(chatRoomId)
                            .userId(tokenUserInfo.getId())
                            .build()
            );

            return webSocketStompService.sendMessage(message);
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }

    /**
     * 채팅방 연결 끊기
     */
    @DeleteMapping("/{chatRoomId}/disconnect")
    public ResponseEntity<Void> disconnectChatRoom(@AuthenticationPrincipal TokenUserInfo tokenUserInfo,
                                                   @PathVariable Long chatRoomId) {
        // 사용자의 채팅방 구독 정보 삭제
        userChatRoomService.removeSubscriberFromChatRoom(chatRoomId, tokenUserInfo.getId());

        CommonResDto<Void> commonResDto = new CommonResDto<>(HttpStatus.OK, "채팅방 연결 끊기 완료", null);
        return new ResponseEntity(commonResDto, HttpStatus.OK);
    }

    /**
     * 메시지 수정
     */
    @PutMapping("/{messageId}/update")
    public Mono<MessageDto> updateMessage(@PathVariable String messageId,
                                          @RequestBody String newContent,
                                          @AuthenticationPrincipal TokenUserInfo tokenUserInfo) {
        return webSocketStompService.updateMessage(messageId, newContent, tokenUserInfo.getId());
    }

    /**
     * 메시지 삭제
     */
    @DeleteMapping("/{messageId}")
    public Mono<Void> deleteMessage(@PathVariable String messageId,
                                    @AuthenticationPrincipal TokenUserInfo tokenUserInfo) {
        return webSocketStompService.deleteMessage(messageId, tokenUserInfo.getId());
    }

    /**
     * 특정 채팅방의 메시지 조회
     */
    @GetMapping("/{chatRoomId}/messages")
    public Flux<MessageDto> getMessages(@PathVariable Long chatRoomId,
                                        @AuthenticationPrincipal TokenUserInfo tokenUserInfo) {
        return webSocketStompService.getMessagesByChatRoom(chatRoomId, tokenUserInfo.getId());
    }

}