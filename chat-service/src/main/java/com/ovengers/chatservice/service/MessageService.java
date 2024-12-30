package com.ovengers.chatservice.service;

import com.ovengers.chatservice.dto.MessageDto;
import com.ovengers.chatservice.entity.Message;
import com.ovengers.chatservice.repository.MessageRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Getter
@Transactional
public class MessageService {
    private final MessageRepository messageRepository;

    // 채팅방마다의 메시지 전체 조회
    public List<MessageDto> getAllMessage(Long chatRoomId) {
        return messageRepository.findAllByOrderByCreatedAtDesc(chatRoomId)
                .stream()
                .map(MessageDto::fromEntity)
                .toList();
    }

    // 메시지 내용 작성
    public MessageDto createMessage(Long chatRoomId, String content) {
        Message message = messageRepository.findByChatRoomId(chatRoomId);
        message.setContent(content);

        return MessageDto.fromEntity(messageRepository.save(message));
    }

    // 메시지 내용 수정
    public MessageDto updateMessage(Long messageId, String newContent) {
        Message message = messageRepository.findById(String.valueOf(messageId))
                .orElseThrow(() -> new EntityNotFoundException("Message not found with id: " + messageId));
        message.setContent(newContent);
        return MessageDto.fromEntity(messageRepository.save(message));
    }

    // 메시지 내용 삭제
    public void deleteMessage(Long messageId) {
        messageRepository.deleteById(String.valueOf(messageId));
    }
}
