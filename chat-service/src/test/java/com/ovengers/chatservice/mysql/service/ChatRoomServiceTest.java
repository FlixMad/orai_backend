package com.ovengers.chatservice.mysql.service;

import com.ovengers.chatservice.client.UserResponseDto;
import com.ovengers.chatservice.mongodb.document.Message;
import com.ovengers.chatservice.mongodb.repository.MessageRepository;
import com.ovengers.chatservice.mysql.dto.ChatRoomDto;
import com.ovengers.chatservice.mysql.entity.ChatRoom;
import com.ovengers.chatservice.mysql.exception.InvalidChatRoomNameException;
import com.ovengers.chatservice.mysql.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ExtendWith(MockitoExtension.class)
class ChatRoomServiceTest {
    private static final Logger logger = LoggerFactory.getLogger(ChatRoomServiceTest.class);

    @Mock
    private ChatRoomRepository chatRoomRepository;
    @Mock
    private UserChatRoomRepository userChatRoomRepository;
    @Mock
    private MessageRepository messageRepository;

    @InjectMocks
    private ChatRoomService chatRoomService;

    private UserResponseDto testUser1;
    private UserResponseDto testUser2;
    private UserResponseDto testUser3;


    @BeforeEach
    void setUp() {
        logger.info("===== 테스트 데이터 초기화 시작 =====");
        testUser1 = new UserResponseDto();
        testUser2 = new UserResponseDto();
        testUser3 = new UserResponseDto();
        logger.info("테스트 사용자 데이터 초기화 완료");
    }

    @Test
    @DisplayName("채팅방 수정 성공 테스트")
    void updateChatRoomSuccess() {
        logger.info("===== 채팅방 수정 테스트 시작 =====");

        // given
        Long chatRoomId = 1L;
        String userId = "user1";
        String newImage = "new.jpg";
        String newName = "수정된 채팅방";

        logger.info("테스트 데이터 설정 - 채팅방 ID: {}, 사용자 ID: {}", chatRoomId, userId);
        logger.info("수정할 정보 - 이름: {}, 이미지: {}", newName, newImage);

        ChatRoom chatRoom = ChatRoom.builder()
                .chatRoomId(chatRoomId)
                .name("기존 채팅방")
                .image("old.jpg")
                .creatorId(userId)
                .build();

        when(chatRoomRepository.findById(chatRoomId)).thenReturn(Optional.of(chatRoom));
        when(userChatRoomRepository.existsByChatRoomIdAndUserId(chatRoomId, userId)).thenReturn(true);
        when(messageRepository.save(any())).thenReturn(Mono.just(new Message()));

        // when
        logger.info("채팅방 수정 실행...");
        ChatRoomDto result = chatRoomService.updateChatRoom(chatRoomId, newImage, newName, userId);

        // then
        logger.info("검증 단계 시작...");
        assertNotNull(result);
        assertEquals(newName, result.getName());
        assertEquals(newImage, result.getImage());
        verify(chatRoomRepository).findById(chatRoomId);
        logger.info("채팅방 수정 결과 - 이름: {}, 이미지: {}", result.getName(), result.getImage());
        logger.info("채팅방 수정 테스트 성공");
    }

    @Test
    @DisplayName("채팅방 생성 실패 - 잘못된 이름")
    void createChatRoomFailWithInvalidName() {
        logger.info("===== 채팅방 생성 실패 테스트 시작 =====");

        // given
        String image = "test.jpg";
        String invalidName = "   ";
        String userId = "user1";
        List<String> userIds = Arrays.asList("user1", "user2");

        logger.info("테스트 데이터 - 잘못된 채팅방 이름: '{}', 사용자 ID: {}", invalidName, userId);
        logger.info("참여 사용자 목록: {}", userIds);

        // when & then
        logger.info("잘못된 이름으로 채팅방 생성 시도...");
        Exception exception = assertThrows(InvalidChatRoomNameException.class, () ->
                chatRoomService.createChatRoom(image, invalidName, userId, userIds)
        );
        logger.error("예상된 예외 발생: {}", exception.getClass().getSimpleName());
        logger.info("채팅방 생성 실패 테스트 성공");
    }

    @Test
    @DisplayName("채팅방 생성자 나가기 실패 테스트")
    void disconnectChatRoomFailForCreator() {
        logger.info("===== 채팅방 생성자 나가기 실패 테스트 시작 =====");

        // given
        Long chatRoomId = 1L;
        String creatorId = "user1";

        logger.info("테스트 데이터 - 채팅방 ID: {}, 생성자 ID: {}", chatRoomId, creatorId);

        ChatRoom chatRoom = ChatRoom.builder()
                .chatRoomId(chatRoomId)
                .name("테스트 채팅방")
                .creatorId(creatorId)
                .build();

        when(chatRoomRepository.findById(chatRoomId)).thenReturn(Optional.of(chatRoom));
        when(userChatRoomRepository.existsByChatRoomIdAndUserId(chatRoomId, creatorId)).thenReturn(true);

        // when & then
        logger.info("채팅방 생성자의 채팅방 나가기 시도...");
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                chatRoomService.disconnectChatRoom(chatRoomId, creatorId)
        );
        logger.error("예상된 예외 발생: {}", exception.getClass().getSimpleName());
        logger.info("채팅방 생성자 나가기 실패 테스트 성공");
    }
}

