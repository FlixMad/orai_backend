package com.ovengers.chatservice.mongodb.service;

import com.ovengers.chatservice.client.UserResponseDto;
import com.ovengers.chatservice.client.UserServiceClient;
import com.ovengers.chatservice.mongodb.document.Message;
import com.ovengers.chatservice.mongodb.dto.MessageDto;
import com.ovengers.chatservice.mongodb.repository.MessageRepository;
import com.ovengers.chatservice.mysql.repository.ChatRoomRepository;
import com.ovengers.chatservice.mysql.repository.UserChatRoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {

    @Mock
    private MessageRepository messageRepository;
    @Mock
    private ChatRoomRepository chatRoomRepository;
    @Mock
    private UserChatRoomRepository userChatRoomRepository;
    @Mock
    private UserServiceClient userServiceClient;

    @InjectMocks
    private MessageService messageService;

    private UserResponseDto testUser;
    private Message testMessage;
    private final Long chatRoomId = 1L;
    private final String userId = "user1";
    private final String userName = "테스트유저";
    private final String messageId = "message1";

    @BeforeEach
    void setUp() {
        testUser = new UserResponseDto();

        testMessage = Message.builder()
                .messageId(messageId)
                .chatRoomId(chatRoomId)
                .content("테스트 메시지")
                .senderId(userId)
                .senderName(userName)
                .senderImage("profile.jpg")
                .type("CHAT")
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("메시지 전송 성공 테스트")
    void sendMessageSuccess() {
        // given
        String content = "테스트 메시지";

        when(chatRoomRepository.existsById(chatRoomId)).thenReturn(true);
        when(userChatRoomRepository.existsByChatRoomIdAndUserId(chatRoomId, userId)).thenReturn(true);
        when(userServiceClient.getUserById(userId)).thenReturn(testUser);
        when(messageRepository.save(any(Message.class))).thenReturn(Mono.just(testMessage));

        // when
        Mono<MessageDto> result = messageService.sendMessage(chatRoomId, content, userId, userName);

        // then
        StepVerifier.create(result)
                .expectNextMatches(messageDto ->
                        messageDto.getContent().equals(content) &&
                                messageDto.getSenderId().equals(userId) &&
                                messageDto.getType().equals("CHAT"))
                .verifyComplete();
    }

    @Test
    @DisplayName("메시지 삭제 성공 테스트")
    void deleteMessageSuccess() {
        // given
        Message deletedMessage = Message.builder()
                .content("메시지가 삭제되었습니다.")
                .type("DELETE")
                .build();

        when(chatRoomRepository.existsById(chatRoomId)).thenReturn(true);
        when(userChatRoomRepository.existsByChatRoomIdAndUserId(chatRoomId, userId)).thenReturn(true);
        when(messageRepository.findByMessageId(messageId)).thenReturn(Mono.just(testMessage));
        when(messageRepository.save(any(Message.class))).thenReturn(Mono.just(deletedMessage));

        // when
        Mono<MessageDto> result = messageService.deleteMessage(chatRoomId, messageId, userId);

        // then
        StepVerifier.create(result)
                .expectNextMatches(messageDto ->
                        messageDto.getContent().equals("메시지가 삭제되었습니다.") &&
                                messageDto.getType().equals("DELETE"))
                .verifyComplete();
    }

    @Test
    @DisplayName("권한 없는 메시지 수정 실패 테스트")
    void updateMessageWithoutPermissionFail() {
        // given
        String otherUserId = "user2";
        String newContent = "수정된 메시지";

        when(chatRoomRepository.existsById(chatRoomId)).thenReturn(true);
        when(userChatRoomRepository.existsByChatRoomIdAndUserId(chatRoomId, otherUserId)).thenReturn(true);
        when(userServiceClient.getUserById(otherUserId)).thenReturn(new UserResponseDto());
        when(messageRepository.findByMessageId(messageId)).thenReturn(Mono.just(testMessage));

        // when & then
        StepVerifier.create(messageService.updateMessage(chatRoomId, messageId, newContent, otherUserId))
                .expectError(IllegalAccessException.class)
                .verify();
    }
}