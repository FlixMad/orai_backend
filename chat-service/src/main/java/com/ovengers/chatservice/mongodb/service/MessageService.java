package com.ovengers.chatservice.mongodb.service;

import com.ovengers.chatservice.mongodb.dto.MessageDto;
import com.ovengers.chatservice.mongodb.document.Message;
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
                .content(messageDto.getContent())
                .readCount(messageDto.getReadCount() != null ? messageDto.getReadCount() : 0L)
                .chatRoomId(messageDto.getChatRoomId())
                .build();

        message = messageRepository.save(message);

        return message.toDto();
    }

    public List<MessageDto> getMessageByChatRoomId(Long ChatRoomId) {
        List<Message> messages = messageRepository.findByChatRoomId(ChatRoomId);
        List<MessageDto> messageDto = new ArrayList<>();
        for (Message message : messages) {
            messageDto.add(message.toDto());
        }

        return messageDto;
    }
}
