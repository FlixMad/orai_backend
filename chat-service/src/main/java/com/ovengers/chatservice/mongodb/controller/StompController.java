package com.ovengers.chatservice.mongodb.controller;

import com.ovengers.chatservice.mongodb.dto.MessageDto;
import com.ovengers.chatservice.mongodb.dto.MessageRequestDto;
import com.ovengers.chatservice.mongodb.service.MessageService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.security.Principal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequiredArgsConstructor
@Tag(name = "StompController", description = "Stomp 통신 관련 컨트롤러")
public class StompController {
    private final MessageService messageService;

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

    @MessageMapping("/{chatRoomId}/send")
    @SendTo("/sub/{chatRoomId}/chat") // 메시지를 구독한 채팅방으로 전달
    public Mono<MessageDto> sendMessage(@DestinationVariable Long chatRoomId,
                                        @RequestBody MessageRequestDto messageRequestDto,
                                        Principal principal) {
        String principalId = principal.getName();
        String senderId = extractIdFromPrincipal(principalId);

        // 메시지 전송을 비동기적으로 처리
        return messageService.sendMessage(chatRoomId, messageRequestDto.getContent(), senderId)
                .onErrorResume(e -> Mono.error(new IllegalStateException("메시지 전송에 실패했습니다.", e)));
    }

    @MessageMapping("/{messageId}/update")
    @SendTo("/sub/{chatRoomId}/chat")
    public Mono<MessageDto> updateMessageViaWebSocket(
            @DestinationVariable String messageId,
            @RequestBody MessageRequestDto messageRequestDto,
            Principal principal) {

        String principalId = principal.getName();
        String senderId = extractIdFromPrincipal(principalId);

        return messageService.updateMessage(
                messageId,
                messageRequestDto.getContent(),
                senderId
        );
    }

    @MessageMapping("/{messageId}/delete")
    @SendTo("/sub/{chatRoomId}/chat")
    public Mono<String> deleteMessageViaWebSocket(
            @DestinationVariable String messageId,
            Principal principal) {

        String principalId = principal.getName();
        String senderId = extractIdFromPrincipal(principalId);

        return messageService.deleteMessage(messageId, senderId)
                .then(Mono.just("Message deleted successfully")); // 성공 메시지를 반환
    }
}
