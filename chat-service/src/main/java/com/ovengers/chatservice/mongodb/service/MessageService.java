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

    // Read
    public Flux<MessageDto> findMessages(Long ChatRoomId) {
        Flux<Message> messages = messageRepository.findAllByChatRoomId(ChatRoomId);
        return messages.map(Message::toDto);
    }

    public Flux<Message> getMessageByChatRoomId(Long ChatRoomId) {
        return messageRepository.findAllByChatRoomId(ChatRoomId);
    }

    // Create
    public Mono<Message> createMessage(Message message) {
        return messageRepository.save(message);
    }

    // Update
    public Mono<Message> updateMessage(String messageId, Message message) {
        return messageRepository.findById(messageId)
                .flatMap(existingMessage -> {
                    existingMessage.setMessageId(message.getMessageId());
                    existingMessage.setContent(message.getContent());
                    existingMessage.setReadCount(message.getReadCount());
                    existingMessage.setCreatedAt(message.getCreatedAt());
                    existingMessage.setChatRoomId(message.getChatRoomId());
                    existingMessage.setUserId(message.getUserId());
                    return messageRepository.save(existingMessage);
                });
    }

    // Delete
    public Mono<Void> deleteMessage(String messageId) {
        return messageRepository.deleteById(messageId);
    }
}
