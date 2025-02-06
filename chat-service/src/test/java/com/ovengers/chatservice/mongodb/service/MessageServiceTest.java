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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {
    private static final Logger logger = LoggerFactory.getLogger(MessageServiceTest.class);

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
        logger.info("===== 테스트 데이터 초기화 시작 =====");
        testUser = new UserResponseDto();
        logger.info("테스트 사용자 초기화 완료");

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
        logger.info("테스트 메시지 초기화 - ID: {}, 내용: {}, 발신자: {}",
                testMessage.getMessageId(), testMessage.getContent(), testMessage.getSenderName());
        logger.info("===== 테스트 데이터 초기화 완료 =====");
    }

    @Test
    @DisplayName("메시지 전송 성공 테스트")
    void sendMessageSuccess() {
        logger.info("===== 메시지 전송 테스트 시작 =====");

        // given
        String content = "테스트 메시지";
        logger.info("테스트 데이터 - 채팅방 ID: {}, 발신자 ID: {}, 메시지 내용: {}",
                chatRoomId, userId, content);

        when(chatRoomRepository.existsById(chatRoomId)).thenReturn(true);
        when(userChatRoomRepository.existsByChatRoomIdAndUserId(chatRoomId, userId)).thenReturn(true);
        when(userServiceClient.getUserById(userId)).thenReturn(testUser);
        when(messageRepository.save(any(Message.class))).thenReturn(Mono.just(testMessage));

        // when
        logger.info("메시지 전송 실행...");
        Mono<MessageDto> result = messageService.sendMessage(chatRoomId, content, userId, userName);

        // then
        logger.info("검증 단계 시작...");
        StepVerifier.create(result)
                .expectNextMatches(messageDto -> {
                    logger.info("전송된 메시지 정보:");
                    logger.info("- 내용: {}", messageDto.getContent());
                    logger.info("- 발신자 ID: {}", messageDto.getSenderId());
                    logger.info("- 메시지 타입: {}", messageDto.getType());
                    return messageDto.getContent().equals(content) &&
                            messageDto.getSenderId().equals(userId) &&
                            messageDto.getType().equals("CHAT");
                })
                .verifyComplete();
        logger.info("메시지 전송 테스트 성공");
        logger.info("============================");
    }

    @Test
    @DisplayName("메시지 삭제 성공 테스트")
    void deleteMessageSuccess() {
        logger.info("===== 메시지 삭제 테스트 시작 =====");

        // given
        logger.info("테스트 데이터 - 채팅방 ID: {}, 메시지 ID: {}, 사용자 ID: {}",
                chatRoomId, messageId, userId);

        Message deletedMessage = Message.builder()
                .content("메시지가 삭제되었습니다.")
                .type("DELETE")
                .build();
        logger.info("삭제될 메시지 생성 완료");

        when(chatRoomRepository.existsById(chatRoomId)).thenReturn(true);
        when(userChatRoomRepository.existsByChatRoomIdAndUserId(chatRoomId, userId)).thenReturn(true);
        when(messageRepository.findByMessageId(messageId)).thenReturn(Mono.just(testMessage));
        when(messageRepository.save(any(Message.class))).thenReturn(Mono.just(deletedMessage));

        // when
        logger.info("메시지 삭제 실행...");
        Mono<MessageDto> result = messageService.deleteMessage(chatRoomId, messageId, userId);

        // then
        logger.info("검증 단계 시작...");
        StepVerifier.create(result)
                .expectNextMatches(messageDto -> {
                    logger.info("삭제된 메시지 정보:");
                    logger.info("- 내용: {}", messageDto.getContent());
                    logger.info("- 메시지 타입: {}", messageDto.getType());
                    return messageDto.getContent().equals("메시지가 삭제되었습니다.") &&
                            messageDto.getType().equals("DELETE");
                })
                .verifyComplete();
        logger.info("메시지 삭제 테스트 성공");
        logger.info("============================");
    }

    @Test
    @DisplayName("권한 없는 메시지 수정 실패 테스트")
    void updateMessageWithoutPermissionFail() {
        logger.info("===== 권한 없는 메시지 수정 테스트 시작 =====");

        // given
        String otherUserId = "user2";
        String newContent = "수정된 메시지";
        logger.info("테스트 데이터:");
        logger.info("- 채팅방 ID: {}", chatRoomId);
        logger.info("- 메시지 ID: {}", messageId);
        logger.info("- 원래 작성자 ID: {}", userId);
        logger.info("- 수정 시도자 ID: {}", otherUserId);
        logger.info("- 수정할 내용: {}", newContent);

        when(chatRoomRepository.existsById(chatRoomId)).thenReturn(true);
        when(userChatRoomRepository.existsByChatRoomIdAndUserId(chatRoomId, otherUserId)).thenReturn(true);
        when(userServiceClient.getUserById(otherUserId)).thenReturn(new UserResponseDto());
        when(messageRepository.findByMessageId(messageId)).thenReturn(Mono.just(testMessage));

        // when & then
        logger.info("권한 없는 메시지 수정 시도...");
        StepVerifier.create(messageService.updateMessage(chatRoomId, messageId, newContent, otherUserId))
                .expectError(IllegalAccessException.class)
                .verify();
        logger.error("예상된 예외 발생: IllegalAccessException");
        logger.info("권한 없는 메시지 수정 실패 테스트 성공");
        logger.info("=====================================");
    }
}