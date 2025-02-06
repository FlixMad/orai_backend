package com.ovengers.chatservice.mysql.service;

import com.ovengers.chatservice.client.UserResponseDto;
import com.ovengers.chatservice.client.UserServiceClient;
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
import org.springframework.messaging.simp.SimpMessagingTemplate;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatRoomServiceTest {

    @Mock
    private ChatRoomRepository chatRoomRepository;
    @Mock
    private UserChatRoomRepository userChatRoomRepository;
    @Mock
    private UserServiceClient userServiceClient;
    @Mock
    private MessageRepository messageRepository;
    @Mock
    private ChatRoomReadRepository chatRoomReadRepository;

    @InjectMocks
    private ChatRoomService chatRoomService;

    private UserResponseDto testUser1;
    private UserResponseDto testUser2;
    private UserResponseDto testUser3;

    @BeforeEach
    void setUp() {
        testUser1 = new UserResponseDto();
        testUser2 = new UserResponseDto();
        testUser3 = new UserResponseDto();
    }

    @Test
    @DisplayName("채팅방 나가기 성공 테스트")
    void disconnectChatRoomSuccess() {
        // given
        Long chatRoomId = 1L;
        String userId = "user2";
        ChatRoom chatRoom = ChatRoom.builder()
                .chatRoomId(chatRoomId)
                .name("테스트 채팅방")
                .creatorId("user1")
                .build();

        when(chatRoomRepository.findById(chatRoomId)).thenReturn(Optional.of(chatRoom));
        when(userChatRoomRepository.existsByChatRoomIdAndUserId(chatRoomId, userId)).thenReturn(true);
        when(userServiceClient.getUserById(userId)).thenReturn(testUser2);
        when(chatRoomRepository.findByChatRoomId(chatRoomId)).thenReturn(chatRoom);
        when(messageRepository.save(any())).thenReturn(Mono.just(new Message()));

        // when
        chatRoomService.disconnectChatRoom(chatRoomId, userId);

        // then
        verify(userChatRoomRepository).deleteByChatRoomIdAndUserId(chatRoomId, userId);
        verify(chatRoomReadRepository).deleteByChatRoomIdAndUserId(chatRoomId, userId);
    }

    @Test
    @DisplayName("채팅방 수정 성공 테스트")
    void updateChatRoomSuccess() {
        // given
        Long chatRoomId = 1L;
        String userId = "user1";
        String newImage = "new.jpg";
        String newName = "수정된 채팅방";

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
        ChatRoomDto result = chatRoomService.updateChatRoom(chatRoomId, newImage, newName, userId);

        // then
        assertNotNull(result);
        assertEquals(newName, result.getName());
        assertEquals(newImage, result.getImage());
        verify(chatRoomRepository).findById(chatRoomId);
    }

    @Test
    @DisplayName("채팅방 생성 실패 - 잘못된 이름")
    void createChatRoomFailWithInvalidName() {
        // given
        String image = "test.jpg";
        String invalidName = "   ";
        String userId = "user1";
        List<String> userIds = Arrays.asList("user1", "user2");

        // when & then
        assertThrows(InvalidChatRoomNameException.class, () ->
                chatRoomService.createChatRoom(image, invalidName, userId, userIds)
        );
    }

    @Test
    @DisplayName("채팅방 생성자 나가기 실패 테스트")
    void disconnectChatRoomFailForCreator() {
        // given
        Long chatRoomId = 1L;
        String creatorId = "user1";
        ChatRoom chatRoom = ChatRoom.builder()
                .chatRoomId(chatRoomId)
                .name("테스트 채팅방")
                .creatorId(creatorId)
                .build();

        when(chatRoomRepository.findById(chatRoomId)).thenReturn(Optional.of(chatRoom));
        when(userChatRoomRepository.existsByChatRoomIdAndUserId(chatRoomId, creatorId)).thenReturn(true);

        // when & then
        assertThrows(IllegalArgumentException.class, () ->
                chatRoomService.disconnectChatRoom(chatRoomId, creatorId)
        );
    }

    public UserResponseDto getTestUser1() {
        return testUser1;
    }

    public void setTestUser1(UserResponseDto testUser1) {
        this.testUser1 = testUser1;
    }

    public UserResponseDto getTestUser3() {
        return testUser3;
    }

    public void setTestUser3(UserResponseDto testUser3) {
        this.testUser3 = testUser3;
    }
}

