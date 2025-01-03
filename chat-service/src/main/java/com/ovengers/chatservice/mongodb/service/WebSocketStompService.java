package com.ovengers.chatservice.mongodb.service;

import com.ovengers.chatservice.mongodb.document.Message;
import com.ovengers.chatservice.mongodb.dto.MessageDto;
import com.ovengers.chatservice.mongodb.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class WebSocketStompService {

    private final MessageRepository messageRepository;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * 메시지 송신
     */
    public Mono<MessageDto> sendMessage(/*Long chatRoomId,*/Message message) {
/*        return messageRepository.save(message)
                .doOnSuccess(savedMessage ->
                        messagingTemplate.convertAndSend("/sub/chat/" + chatRoomId, savedMessage.toDto())
                )
                .map(Message::toDto);*/
        return messageRepository.save(message)
                .map(Message::toDto);
    }

    /**
     * 메시지 수정
     */
    public Mono<MessageDto> updateMessage(String messageId, String newContent) {
        return messageRepository.findById(messageId)
                .flatMap(existingMessage -> {
                    existingMessage.setContent(newContent);
                    return messageRepository.save(existingMessage);
                })
                .map(Message::toDto)
                .doOnSuccess(updatedMessage ->
                        messagingTemplate.convertAndSend("/sub/chat/" + updatedMessage.getChatRoomId(), updatedMessage)
                );
    }

    /**
     * 메시지 삭제
     */
    public Mono<Void> deleteMessage(String messageId) {
        return messageRepository.findById(messageId)
                .flatMap(existingMessage -> {
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
    public Flux<MessageDto> getMessagesByChatRoom(Long chatRoomId) {
        return messageRepository.findAllByChatRoomId(chatRoomId)
                .map(Message::toDto);
    }
}
