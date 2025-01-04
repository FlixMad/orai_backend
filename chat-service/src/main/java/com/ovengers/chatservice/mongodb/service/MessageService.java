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

    /**
     * MongoDB에 저장된 chatRoomId 마다의 모든 데이터 조회
     */
    public Flux<MessageDto> getMessages(Long chatRoomId) {
        Flux<Message> messages = messageRepository.findAllByChatRoomId(chatRoomId);
        return messages.map(Message::toDto);
    }

    /**
     * MongoDB에 데이터 저장
     * (JSON - {"content":"Hello", "chatRoomId": 1})
     */
    public Mono<MessageDto> createMessage(Message message) {
        return messageRepository.save(message)
                .map(Message::toDto);
    }

    /**
     * MongoDB에 저장된 데이터 중 해당 messageId를 가진 content 수정
     * (JSON - {"content":"Hi"})
     */
    public Mono<MessageDto> updateMessage(String messageId, String newContent) {
        return messageRepository.findById(messageId)
                .flatMap(existingMessage -> {
                    existingMessage.setContent(newContent);
                    return messageRepository.save(existingMessage);
                })
                .map(Message::toDto);
    }

    /**
     * MongoDB에 저장된 데이터 중 해당 messageId를 가진 데이터 삭제
     */
    public Mono<Void> deleteMessage(String messageId) {
        return messageRepository.deleteById(messageId);
    }
}
