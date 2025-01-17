package com.ovengers.chatservice.mongodb.controller;

import com.ovengers.chatservice.mongodb.dto.MessageDto;
import com.ovengers.chatservice.mongodb.dto.MessageRequestDto;
import com.ovengers.chatservice.mongodb.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.security.Principal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequiredArgsConstructor
@Tag(name = "MessageController", description = "단순히 MongoDB에 데이터를 저장하는 컨트롤러")
public class MessageController {
    private final MessageService messageService;

    /**
     * Principal에서 id를 추출하는 유틸리티 메서드
     *
     * @param principalName Principal에서 가져온 name 값
     * @return 추출된 id
     */
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

    @Operation(summary = "메시지 저장", description = "채팅방Id, 콘텐츠")
    @PostMapping("/{chatRoomId}/saveMessage")
    public Mono<MessageDto> saveMessage(@PathVariable Long chatRoomId,
                                        @RequestBody MessageRequestDto messageRequestDto,
                                        Principal principal) {
        String principalId = principal.getName();
        String senderId = extractIdFromPrincipal(principalId);

        return messageService.sendMessage(chatRoomId, messageRequestDto.getContent(), senderId);
    }

    @Operation(summary = "채팅방의 메시지 조회", description = "채팅방Id")
    @GetMapping("/{chatRoomId}/messageList")
    public Flux<MessageDto> getMessages(@PathVariable Long chatRoomId,
                                        Principal principal) {
        String principalId = principal.getName();
        String senderId = extractIdFromPrincipal(principalId);

        return messageService.getMessages(chatRoomId, senderId);
    }

    @Operation(summary = "메시지 수정", description = "채팅방Id, 메시지Id, 콘텐츠")
    @PutMapping("/{chatRoomId}/{messageId}/updateMessage")
    public Mono<MessageDto> updateMessage(@PathVariable Long chatRoomId,
                                          @PathVariable String messageId,
                                          @RequestBody MessageRequestDto messageRequestDto,
                                          Principal principal) {
        String principalId = principal.getName();
        String senderId = extractIdFromPrincipal(principalId);

        return messageService.updateMessage(chatRoomId, messageId, messageRequestDto.getContent(), senderId);
    }

    @Operation(summary = "메시지 삭제", description = "채팅방Id, 메시지Id")
    @DeleteMapping("/{chatRoomId}/{messageId}/deleteMessage")
    public Mono<Void> deleteMessage(@PathVariable Long chatRoomId,
                                    @PathVariable String messageId,
                                    Principal principal) {
        String principalId = principal.getName();
        String senderId = extractIdFromPrincipal(principalId);

        return messageService.deleteMessage(chatRoomId, messageId, senderId);
    }

//    public String cleanInput(String input) {
//        if (input == null) {
//            return null;
//        }
//        // 문자열 양 끝의 쌍따옴표만 제거
//        return input.startsWith("\"") && input.endsWith("\"")
//                ? input.substring(1, input.length() - 1)
//                : input;
//    }

}
