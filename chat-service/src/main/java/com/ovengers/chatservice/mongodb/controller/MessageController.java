package com.ovengers.chatservice.mongodb.controller;

import com.ovengers.chatservice.mongodb.dto.MessageDto;
import com.ovengers.chatservice.mongodb.dto.MessageRequestDto;
import com.ovengers.chatservice.mongodb.service.MessageService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequiredArgsConstructor
@Tag(name = "MessageController", description = "단순히 MongoDB에 데이터를 저장하는 컨트롤러")
public class MessageController {
    private final MessageService messageService;
    private final SimpMessagingTemplate messagingTemplate;

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

    @MessageMapping("/chat/send")
    public void sendMessage(Long chatRoomId, @RequestBody MessageRequestDto messageRequestDto, Principal principal) {
        String principalId = principal.getName();
        String senderId = extractIdFromPrincipal(principalId);

        // 서비스 메서드를 호출하여 메시지 저장 및 DTO 변환
        MessageDto messageDto = messageService.sendMessage(chatRoomId, content, userId).block();

        if (messageDto == null) {
            throw new IllegalStateException("메시지 전송에 실패했습니다.");
        }

        // 채팅방이 1:1 채팅인지 그룹 채팅인지는 단순히 채팅방 ID로 구분되므로
        // 모두 동일한 방식으로 처리됨
        if (messageDto.getReceiverId() != null) {
            // 1:1 채팅일 경우
            messagingTemplate.convertAndSendToUser(
                    messageDto.getReceiverId(),  // 수신자 ID
                    "/queue/messages",           // 개인 메시지 목적지
                    messageDto                   // 전송할 메시지 DTO
            );
        } else {
            // 그룹 채팅일 경우
            messagingTemplate.convertAndSend(
                    "/topic/chat/" + chatRoomId, // 그룹 채팅 목적지
                    messageDto                   // 전송할 메시지 DTO
            );
        }
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
//
//    /**
//     * 데이터 저장
//     */
//    @PostMapping("/{chatRoomId}/createMessage")
//    @ResponseStatus(HttpStatus.CREATED)
//    public Mono<MessageDto> createMessage(
//            @PathVariable Long chatRoomId,
//            @RequestBody String content,
//            Principal principal
//    ) {
//        String cleanedContent = cleanInput(content);
//
//        try {
//            // principal.getName()에서 id 파싱
//            String principalName = principal.getName();
//            String senderId = extractIdFromPrincipal(principalName);
//
//            // Message 객체 생성
//            Message message = new Message();
//            message.setChatRoomId(chatRoomId);
//            message.setContent(cleanedContent);
//            message.setSenderId(senderId);
//
//            return messageService.createMessage(message);
//        } catch (IllegalArgumentException ex) {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
//        }
//    }
//

//    }

}
