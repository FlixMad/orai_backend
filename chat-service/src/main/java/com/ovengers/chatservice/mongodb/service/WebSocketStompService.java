package com.ovengers.chatservice.mongodb.service;

import com.ovengers.chatservice.common.auth.TokenUserInfo;
import com.ovengers.chatservice.mongodb.document.Message;
import com.ovengers.chatservice.mongodb.dto.MessageDto;
import com.ovengers.chatservice.mongodb.repository.MessageRepository;
import com.ovengers.chatservice.mysql.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class WebSocketStompService {

    private final MessageRepository messageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * 메시지 송신
     */
    public Mono<MessageDto> sendMessage(Message message, TokenUserInfo tokenUserInfo) {
        return Mono.fromCallable(() -> chatRoomRepository.existsById(message.getChatRoomId()))
                .flatMap(chatRoomExists -> {
                    if (chatRoomExists) {
                        message.setSenderId(tokenUserInfo.getId()); // JWT 인증된 사용자 설정
                        return messageRepository.save(message).map(Message::toDto);
                    } else {
                        return Mono.error(new IllegalArgumentException(message.getChatRoomId() + "번 채팅방은 존재하지 않습니다."));
                    }
                });
    }

    /**
     * 메시지 수정
     */
    public Mono<MessageDto> updateMessage(String messageId, String newContent, String senderId) {
        return messageRepository.findById(messageId)
                .flatMap(existingMessage -> {
                    if (!existingMessage.getSenderId().equals(senderId)) {
                        return Mono.error(new SecurityException("권한이 없습니다."));
                    }
                    existingMessage.setContent(newContent);
                    return messageRepository.save(existingMessage);
                })
                .map(Message::toDto)
                .doOnSuccess(updatedMessage ->
                        messagingTemplate.convertAndSend("/sub/" + updatedMessage.getChatRoomId() + "/chat/", updatedMessage)
                );
    }


    /**
     * 메시지 삭제
     */
    public Mono<Void> deleteMessage(String messageId, String senderId) {
        return messageRepository.findById(messageId)
                .flatMap(existingMessage -> {
                    if (!existingMessage.getSenderId().equals(senderId)) {
                        return Mono.error(new SecurityException("권한이 없습니다."));
                    }
                    messagingTemplate.convertAndSend(
                            "/sub/chat/" + existingMessage.getChatRoomId(),
                            "Message deleted: " + messageId
                    );
                    return messageRepository.delete(existingMessage);
                });
    }

    /**
     * 채팅방 메시지 조회
     */
    public Flux<MessageDto> getMessagesByChatRoom(Long chatRoomId, String senderId) {
        return Mono.fromCallable(() -> chatRoomRepository.existsById(chatRoomId))
                .flatMapMany(chatRoomExists -> {
                    if (chatRoomExists) {
                        return messageRepository.findAllByChatRoomId(chatRoomId)
                                .map(Message::toDto);
                    } else {
                        return Flux.error(new IllegalArgumentException(chatRoomId + "번 채팅방은 존재하지 않습니다."));
                    }
                });
    }

}
