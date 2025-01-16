package com.ovengers.chatservice.mongodb.controller;

import com.ovengers.chatservice.mongodb.dto.ChatMessageRequest;
import com.ovengers.chatservice.mongodb.dto.ChatMessageResponse;
import com.ovengers.chatservice.mongodb.service.WebSocketStompService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@Tag(name = "WebSocketStompController", description = "유튜브 참고")
public class WebSocketStompController {
    private final WebSocketStompService webSocketStompService;

/*    private String extractIdFromPrincipal(String principalName) {
        // 정규식으로 id 값 추출
        Pattern pattern = Pattern.compile("id=([a-f0-9-]+)");
        Matcher matcher = pattern.matcher(principalName);

        if (matcher.find()) {
            return matcher.group(1); // 첫 번째 그룹(id 값) 반환
        } else {
            throw new IllegalArgumentException("Invalid Principal format");
        }
    }*/

    @MessageMapping("/chat.{chatRoomId}")
    @SendTo("/sub/chat.{chatRoomId}")
    public ChatMessageResponse sendMessage(@DestinationVariable Long chatRoomId,/* Principal principal,*/ ChatMessageRequest request) {
//        String principalId = principal.getName();
//        String senderId = extractIdFromPrincipal(principalId);

        // WebSocket 메시지를 처리한 후 MongoDB에 메시지 저장
        webSocketStompService.saveMessage(chatRoomId,/* senderId,*/ request.content())
                .doOnTerminate(() -> {
                    // 메시지가 저장된 후 어떤 작업을 할 수 있습니다. (예: 로그, 알림 등)
                }).subscribe(); // 비동기 처리

        return new ChatMessageResponse(request.content());
    }

    @MessageMapping("/chat.{chatRoomId}.messages")
    @SendTo("/sub/chat.{chatRoomId}.messages")
    public Flux<ChatMessageResponse> getMessages(@DestinationVariable Long chatRoomId) {
        return webSocketStompService.findMessagesByChatRoomId(chatRoomId)
                .map(dto -> new ChatMessageResponse(dto.getContent()));
    }

    @MessageMapping("/chat.{chatRoomId}.update.{messageId}")
    @SendTo("/sub/chat.{chatRoomId}")
    public Mono<ChatMessageResponse> updateMessage(
            @DestinationVariable Long chatRoomId,
            @DestinationVariable String messageId,
            ChatMessageRequest request
    ) {
        return webSocketStompService.updateMessage(messageId, request.content())
                .map(dto -> new ChatMessageResponse(dto.getContent()));
    }

    @MessageMapping("/chat.{chatRoomId}.delete.{messageId}")
    @SendTo("/sub/chat.{chatRoomId}")
    public Mono<Void> deleteMessage(
            @DestinationVariable Long chatRoomId,
            @DestinationVariable String messageId
    ) {
        return webSocketStompService.deleteMessage(messageId);
    }
}
