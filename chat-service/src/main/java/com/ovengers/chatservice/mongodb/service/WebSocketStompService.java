package com.ovengers.chatservice.mongodb.service;

import com.ovengers.chatservice.mongodb.document.Message;
import com.ovengers.chatservice.mongodb.dto.MessageDto;
import com.ovengers.chatservice.mongodb.repository.MessageRepository;
import com.ovengers.chatservice.mysql.repository.ChatRoomRepository;
import com.ovengers.chatservice.mysql.repository.UserChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class WebSocketStompService {

    private final MessageRepository messageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final UserChatRoomRepository userChatRoomRepository;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * 메시지 송신
     */
    public Mono<MessageDto> sendMessage(Message message) {
        // chatRoomId와 userId 유효성 확인
        return Mono.fromCallable(() -> chatRoomRepository.existsById(message.getChatRoomId()))
                .flatMap(exists -> {
                    if (!exists) {
                        return Mono.error(new IllegalArgumentException(message.getChatRoomId() + "번 채팅방은 존재하지 않습니다."));
                    }
                    // 사용자가 채팅방에 구독되어 있는지 확인
                    return Mono.fromCallable(() -> userChatRoomRepository.existsByChatRoomIdAndUserId(
                            message.getChatRoomId(), message.getSenderId()
                    )).flatMap(isSubscribed -> {
                        if (!isSubscribed) {
                            return Mono.error(new IllegalArgumentException(
                                    "사용자 " + message.getSenderId() + "는 " + message.getChatRoomId() + "번 채팅방에 구독되어 있지 않습니다."
                            ));
                        }
                        // MongoDB에 메시지 저장
                        return messageRepository.save(message).map(Message::toDto)
                                .doOnSuccess(createMessage ->
                                        messagingTemplate.convertAndSend(
                                                "/sub/" + createMessage.getChatRoomId() + "/chat", createMessage
                                        )
                                );
                    });
                });
    }


    /**
     * 메시지 수정
     */
    public Mono<MessageDto> updateMessage(String messageId, String newContent, String senderId) {
        return messageRepository.findById(messageId)
                .flatMap(existingMessage -> {
                    if (!existingMessage.getSenderId().equals(senderId)) {
                        return Mono.error(new SecurityException("권한이 없습니다."));
                    }
                    existingMessage.setContent(newContent);
                    return messageRepository.save(existingMessage);
                })
                .map(Message::toDto)
                .doOnSuccess(updatedMessage ->
                        messagingTemplate.convertAndSend("/sub/" + updatedMessage.getChatRoomId() + "/chat", updatedMessage)
                );
    }

    /**
     * 메시지 삭제
     */
    public Mono<Void> deleteMessage(String messageId, String senderId) {
        return messageRepository.findById(messageId)
                .flatMap(existingMessage -> {
                    if (!existingMessage.getSenderId().equals(senderId)) {
                        return Mono.error(new SecurityException("권한이 없습니다."));
                    }
                    messagingTemplate.convertAndSend(
                            "/sub/" + existingMessage.getChatRoomId() + "/chat",
                            messageId + "메시지를 삭제했습니다."
                    );
                    return messageRepository.delete(existingMessage);
                });
    }

    /**
     * 채팅방 메시지 조회
     */
    public Flux<MessageDto> getMessagesByChatRoom(Long chatRoomId, String senderId) {
        // chatRoomId와 userId 유효성 확인
        return Mono.fromCallable(() -> chatRoomRepository.existsById(chatRoomId))
                .flatMapMany(chatRoomExists -> {
                    if (!chatRoomExists) {
                        return Flux.error(new IllegalArgumentException(chatRoomId + "번 채팅방은 존재하지 않습니다."));
                    }
                    // 사용자가 채팅방에 구독되어 있는지 확인
                    return Mono.fromCallable(() -> userChatRoomRepository.existsByChatRoomIdAndUserId(chatRoomId, senderId))
                            .flatMapMany(isSubscribed -> {
                                if (!isSubscribed) {
                                    return Flux.error(new IllegalArgumentException(
                                            "사용자 " + senderId + "는 " + chatRoomId + "번 채팅방에 구독되어 있지 않습니다."
                                    ));
                                }
                                // 메시지 조회
                                return messageRepository.findAllByChatRoomId(chatRoomId)
                                        .map(Message::toDto);
                            });
                });
    }

}
