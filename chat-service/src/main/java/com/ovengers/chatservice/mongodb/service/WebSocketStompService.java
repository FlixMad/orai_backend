package com.ovengers.chatservice.mongodb.service;

import com.ovengers.chatservice.mongodb.document.Message;
import com.ovengers.chatservice.mongodb.dto.MessageDto;
import com.ovengers.chatservice.mongodb.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class WebSocketStompService {
    private final MessageRepository messageRepository; // MongoDB 저장소

    // 메시지 저장
    public Mono<MessageDto> saveMessage(Long chatRoomId,/* String senderId,*/ String content) {
        Message message = Message.builder()
                .chatRoomId(chatRoomId)
//                .senderId(senderId)
                .content(content)
                .build();

        return messageRepository.save(message).map(Message::toDto); // MongoDB에 저장
    }

    // 메시지 조회
    public Flux<MessageDto> findMessagesByChatRoomId(Long chatRoomId) {
        return messageRepository.findAllByChatRoomId(chatRoomId)
                .map(Message::toDto);
    }

    // 메시지 수정
    public Mono<MessageDto> updateMessage(String messageId, String newContent) {
        return messageRepository.findById(messageId)
                .flatMap(message -> {
                    message.setContent(newContent);
                    return messageRepository.save(message);
                })
                .map(Message::toDto);
    }

    // 메시지 삭제
    public Mono<Void> deleteMessage(String messageId) {
        return messageRepository.deleteById(messageId);
    }
}
