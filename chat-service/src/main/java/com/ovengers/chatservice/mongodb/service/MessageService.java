package com.ovengers.chatservice.mongodb.service;

import com.ovengers.chatservice.mongodb.dto.MessageDto;
import com.ovengers.chatservice.mongodb.document.Message;
import com.ovengers.chatservice.mongodb.repository.MessageRepository;
import com.ovengers.chatservice.mysql.repository.ChatRoomRepository;
import com.ovengers.chatservice.mysql.repository.UserChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final UserChatRoomRepository userChatRoomRepository;

    private void validateMessageContent(String content) {
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("메시지 내용이 비어 있습니다.");
        }
    }

    // 메시지 전송
    public Mono<MessageDto> sendMessage(Long chatRoomId, String content, String userId) {
        if (!chatRoomRepository.existsById(chatRoomId)) {
            throw new IllegalArgumentException(chatRoomId + "번 채팅방은 존재하지 않습니다.");
        }

        if (!userChatRoomRepository.existsByChatRoomIdAndUserId(chatRoomId, userId)) {
            throw new IllegalArgumentException(chatRoomId + "번 채팅방에 구독되어 있지 않습니다.");
        }

        validateMessageContent(content.trim());

        Message message = Message.builder()
                .chatRoomId(chatRoomId)
                .content(content)
                .senderId(userId)
                .build();

        // 메시지 저장 및 DTO로 변환 후 반환
        return messageRepository.save(message).map(Message::toDto);
    }

    // 메시지 조회
    public Flux<MessageDto> getMessages(Long chatRoomId, String userId) {
        if (!chatRoomRepository.existsById(chatRoomId)) {
            throw new IllegalArgumentException(chatRoomId + "번 채팅방은 존재하지 않습니다.");
        }

        if (!userChatRoomRepository.existsByChatRoomIdAndUserId(chatRoomId, userId)) {
            throw new IllegalArgumentException(chatRoomId + "번 채팅방에 구독되어 있지 않습니다.");
        }

        // 메시지 조회
        return messageRepository.findAllByChatRoomId(chatRoomId)
                .map(Message::toDto);
    }

    // 메시지 수정
    public Mono<MessageDto> updateMessage(Long chatRoomId, String messageId, String newContent, String userId) {
        // 채팅방 존재 여부 확인
        if (!chatRoomRepository.existsById(chatRoomId)) {
            throw new IllegalArgumentException(chatRoomId + "번 채팅방은 존재하지 않습니다.");
        }

        // 유저가 채팅방에 속해 있는지 확인
        if (!userChatRoomRepository.existsByChatRoomIdAndUserId(chatRoomId, userId)) {
            throw new IllegalArgumentException(chatRoomId + "번 채팅방에 구독되어 있지 않습니다.");
        }

        return messageRepository.findByMessageId(messageId)
                .flatMap(existingMessage -> {
                    // 메시지 작성자가 아닌 경우 권한 에러 반환
                    if (!existingMessage.getSenderId().equals(userId)) {
                        return Mono.error(new IllegalAccessException("메시지를 수정할 권한이 없습니다."));
                    }

                    // 수정 사항이 없는 경우 에러 반환
                    if (existingMessage.getContent().equals(newContent.trim())) {
                        return Mono.error(new IllegalArgumentException("메시지에 수정 사항이 없습니다."));
                    }

                    // 새로운 메시지 내용 유효성 검사
                    validateMessageContent(newContent.trim());

                    // 메시지 수정
                    existingMessage.setContent(newContent.trim());
                    existingMessage.setUpdatedAt(LocalDateTime.now());

                    return messageRepository.save(existingMessage)
                            .map(Message::toDto);
                })
                .switchIfEmpty(Mono.error(new IllegalArgumentException("메시지가 존재하지 않습니다.")));
    }

    // 메시지 삭제
    public Mono<Void> deleteMessage(Long chatRoomId, String messageId, String userId) {
        if (!chatRoomRepository.existsById(chatRoomId)) {
            throw new IllegalArgumentException(chatRoomId + "번 채팅방은 존재하지 않습니다.");
        }

        if (!userChatRoomRepository.existsByChatRoomIdAndUserId(chatRoomId, userId)) {
            throw new IllegalArgumentException(chatRoomId + "번 채팅방에 구독되어 있지 않습니다.");
        }

        return messageRepository.findByMessageId(messageId)
                .flatMap(delete -> {
                    if (!delete.getSenderId().equals(userId)) {
                        return Mono.error(new IllegalAccessException("메시지를 삭제할 권한이 없습니다."));
                    }
                    return messageRepository.delete(delete);
                });
    }
}
