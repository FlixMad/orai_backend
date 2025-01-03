package com.ovengers.chatservice.mongodb.service;

import com.ovengers.chatservice.mongodb.dto.MessageDto;
import com.ovengers.chatservice.mongodb.document.Message;
import com.ovengers.chatservice.mongodb.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;

    public Flux<MessageDto> findMessages(Long ChatRoomId) {
        Flux<Message> messages = messageRepository.findAllByChatRoomId(ChatRoomId);
        return messages.map(Message::toDto);
    }

    // 채팅방마다의 전체 메시지
    public Flux<MessageDto> getMessages(Long ChatRoomId) {
        Flux<Message> messages = messageRepository.findAllByChatRoomId(ChatRoomId);
        return messages.map(Message::toDto);
    }

    // 메시지 전송
    public Mono<Message> createMessage(Message message) {
        return messageRepository.save(message);
    }

    // 메시지 수정
    public Mono<Message> updateMessage(String messageId, String newContent) {
        return messageRepository.findById(messageId)
                .flatMap(existingMessage -> {
                    existingMessage.setContent(newContent);
                    return messageRepository.save(existingMessage);
                });
    }

    // 메시지 삭제
    public Mono<Void> deleteMessage(String messageId) {
        return messageRepository.deleteById(messageId);
    }
}
