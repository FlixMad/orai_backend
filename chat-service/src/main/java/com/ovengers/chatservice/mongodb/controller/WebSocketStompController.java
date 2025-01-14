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
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@Tag(name = "WebSocketStompController", description = "웹소켓 STOMP 관련 controller")
public class WebSocketStompController {

    private final WebSocketStompService webSocketStompService;
    private final UserChatRoomService userChatRoomService;
    private final SimpMessagingTemplate messagingTemplate;

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
     * 메시지 송신 (STOMP 기반)
     */
    @MessageMapping("/{chatRoomId}/send")
    @SendTo("/sub/{chatRoomId}/chat")
    public Mono<MessageDto> sendMessage(@DestinationVariable Long chatRoomId,
                                        @RequestBody String content,
                                        Principal principal) {

        String cleanedContent = cleanInput(content);

        try {
            Message message = new Message();
            message.setContent(cleanedContent);
            message.setChatRoomId(chatRoomId);
            message.setSenderId(principal.getName());

            // 채팅방 구독 정보 저장
            userChatRoomService.subscribeToChatRoom(
                    UserChatRoomDto.builder()
                            .chatRoomId(chatRoomId)
                            .userId(principal.getName())
                            .build()
            );

            return webSocketStompService.sendMessage(message);
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }


    /**
     * 1:1 채팅 메시지 처리 (특정 사용자에게 메시지 전달)
     */
    @MessageMapping("/chat/one-on-one/{receiverId}") // /pub/chat/one-on-one/{receiverId}
    public void sendOneOnOneMessage(MessageDto messageDto, @DestinationVariable String receiverId) {
        // 메시지 수신 후, 대상에게 메시지 전달
        messagingTemplate.convertAndSend("/sub/chat/one-on-one/" + receiverId, messageDto);
    }

    /**
     * 그룹 채팅 메시지 처리 (그룹에 속한 모든 사용자에게 메시지 전달)
     */
    @MessageMapping("/chat/group/{chatRoomId}") // /pub/chat/group/{chatRoomId}
    public void sendGroupMessage(MessageDto messageDto, @DestinationVariable String chatRoomId) {
        // 그룹 채팅방에 있는 사용자들에게 메시지 전달
        messagingTemplate.convertAndSend("/sub/chat/group/" + chatRoomId, messageDto);
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