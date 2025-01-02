package com.ovengers.chatservice.mongodb.service;

import com.ovengers.chatservice.mongodb.dto.MessageDto;
import com.ovengers.chatservice.mongodb.document.Message;
import com.ovengers.chatservice.mongodb.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;

    public MessageDto saveMessage(MessageDto messageDto) {

        Message message = Message.builder()
                .content(messageDto.getContent())
                .readCount(messageDto.getReadCount() != null ? messageDto.getReadCount() : 0L)
                .chatRoomId(messageDto.getChatRoomId())
                .build();

        message = messageRepository.save(message).block();

        return Objects.requireNonNull(message).toDto();
    }

    public Flux<MessageDto> findMessages(Long ChatRoomId) {
        Flux<Message> messages = messageRepository.findAllByChatRoomId(ChatRoomId);
        return messages.map(Message::toDto);
    }
}
