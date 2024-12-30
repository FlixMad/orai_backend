package com.ovengers.chatservice.mongodb.service;

import com.ovengers.chatservice.mongodb.dto.MessageDto;
import com.ovengers.chatservice.mongodb.entity.Message;
import com.ovengers.chatservice.mongodb.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;

    public MessageDto saveChat(MessageDto messageDto) {

        Message message = Message.builder()
                .messageId(messageDto.getMessageId())
                .content(messageDto.getContent())
                .readCount(messageDto.getReadCount())
                .createdAt(messageDto.getCreatedAt())
                .chatRoomId(messageDto.getChatRoomId())
                .build();

        return MessageDto.fromEntity(messageRepository.save(message));
    }

    public List<MessageDto> getMessageByChatRoomId(Long ChatRoomId) {
        List<Message> messages = messageRepository.findByChatRoomId(ChatRoomId);
        List<MessageDto> messageDtos = new ArrayList<>();
        for (Message message : messages) {
            messageDtos.add(MessageDto.fromEntity(message));
        }

        return messageDtos;
    }
}
