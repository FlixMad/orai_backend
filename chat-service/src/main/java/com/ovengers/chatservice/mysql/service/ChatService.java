package com.ovengers.chatservice.mysql.service;

import com.ovengers.chatservice.mongodb.document.Message;
import com.ovengers.chatservice.mongodb.repository.MessageRepository;
import com.ovengers.chatservice.mysql.dto.ChatRoomUnreadDto;
import com.ovengers.chatservice.mysql.entity.ChatRoom;
import com.ovengers.chatservice.mysql.entity.ChatRoomRead;
import com.ovengers.chatservice.mysql.entity.UserChatRoom;
import com.ovengers.chatservice.mysql.repository.ChatRoomReadRepository;
import com.ovengers.chatservice.mysql.repository.ChatRoomRepository;
import com.ovengers.chatservice.mysql.repository.UserChatRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {
    private final UserChatRoomRepository userChatRoomRepository;
    private final ChatRoomReadRepository chatRoomReadRepository;
    private final MessageRepository messageRepository;
    private final ChatRoomRepository chatRoomRepository;

    public Mono<List<ChatRoomUnreadDto>> getChatRoomsWithUnreadCount(String userId) {
        List<UserChatRoom> userChatRooms = userChatRoomRepository.findAllByUserId(userId);

        if (userChatRooms.isEmpty()) {
            return Mono.just(Collections.emptyList()); // empty() 대신 빈 리스트 반환
        }

        return Flux.fromIterable(userChatRooms)
                .flatMap(userChatRoom -> {
                    Long chatRoomId = userChatRoom.getChatRoomId();
                    ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                            .orElseThrow(() -> new RuntimeException("채팅방을 찾을 수 없습니다."));

                    // 마지막 메시지 조회 수정
                    Mono<Message> lastMessageMono = messageRepository
                            .findByChatRoomIdOrderByCreatedAtDesc(chatRoomId) // Asc -> Desc로 변경
                            .next() // last() -> next()로 변경
                            .defaultIfEmpty(Message.builder() // 기본값 설정
                                    .content("")
                                    .createdAt(LocalDateTime.now())
                                    .build());

                    // 읽지 않은 메시지 수 조회
                    Mono<Long> unreadCountMono = getUnreadCountExcludingSystem(chatRoomId, userId);

                    return Mono.zip(lastMessageMono, unreadCountMono)
                            .map(tuple -> {
                                Message lastMessage = tuple.getT1();
                                Long unreadCount = tuple.getT2();

                                return ChatRoomUnreadDto.builder()
                                        .chatRoomId(chatRoomId)
                                        .name(chatRoom.getName())
                                        .image(chatRoom.getImage())
                                        .creatorId(chatRoom.getCreatorId()) // 방장 ID 추가
                                        .unreadCount(unreadCount)
                                        .lastMessage(lastMessage.getContent())
                                        .lastMessageTime(lastMessage.getCreatedAt() != null
                                                ? lastMessage.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                                                : null)
                                        .build();
                            });
                })
                .collectList()
                .onErrorMap(e -> {
                    log.error("채팅방 목록 조회 중 오류 발생: {}", e.getMessage());
                    return new RuntimeException("채팅방 목록을 불러오는데 실패했습니다.", e);
                });
    }

    @Transactional
    public void updateLastReadMessage(Long chatRoomId, String userId, String messageId) {
        ChatRoomRead chatRoomRead = chatRoomReadRepository
                .findByChatRoomIdAndUserId(chatRoomId, userId)
                .orElseGet(() -> ChatRoomRead.builder()
                        .chatRoomId(chatRoomId)
                        .userId(userId)
                        .unreadCount(0L)
                        .build());

        chatRoomRead.setLastReadMessageId(messageId);
        chatRoomRead.setUnreadCount(0L);
        chatRoomReadRepository.save(chatRoomRead);
    }

    @Transactional
    public void incrementUnreadCount(Long chatRoomId, String senderId) {
        List<UserChatRoom> subscribers = userChatRoomRepository.findAllByChatRoomId(chatRoomId);

        subscribers.stream()
                .filter(subscriber -> !subscriber.getUserId().equals(senderId))
                .forEach(subscriber -> {
                    ChatRoomRead chatRoomRead = chatRoomReadRepository
                            .findByChatRoomIdAndUserId(chatRoomId, subscriber.getUserId())
                            .orElseGet(() -> ChatRoomRead.builder()
                                    .chatRoomId(chatRoomId)
                                    .userId(subscriber.getUserId())
                                    .unreadCount(0L)
                                    .build());

                    chatRoomRead.setUnreadCount(chatRoomRead.getUnreadCount() + 1);
                    chatRoomReadRepository.save(chatRoomRead);
                });
    }

    private Mono<Long> getUnreadCountExcludingSystem(Long chatRoomId, String userId) {
        ChatRoomRead chatRoomRead = chatRoomReadRepository
                .findByChatRoomIdAndUserId(chatRoomId, userId)
                .orElse(null);

        if (chatRoomRead == null || chatRoomRead.getLastReadMessageId() == null) {
            return messageRepository.findByChatRoomIdAndTypeNotOrderByCreatedAtAsc(chatRoomId, "SYSTEM").count();
        }

        return messageRepository.findByMessageId(chatRoomRead.getLastReadMessageId())
                .flatMap(lastReadMessage ->
                        messageRepository.findByChatRoomIdAndTypeNotOrderByCreatedAtAsc(chatRoomId, "SYSTEM")
                                .filter(message -> message.getCreatedAt().isAfter(lastReadMessage.getCreatedAt()))
                                .count()
                );
    }
}