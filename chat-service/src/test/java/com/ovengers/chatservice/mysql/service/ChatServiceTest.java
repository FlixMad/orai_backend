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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    @Mock
    private UserChatRoomRepository userChatRoomRepository;
    @Mock
    private ChatRoomReadRepository chatRoomReadRepository;
    @Mock
    private MessageRepository messageRepository;
    @Mock
    private ChatRoomRepository chatRoomRepository;

    @InjectMocks
    private ChatService chatService;

    private ChatRoom testChatRoom;
    private UserChatRoom testUserChatRoom;
    private Message testMessage;
    private ChatRoomRead testChatRoomRead;

    @BeforeEach
    void setUp() {
        testChatRoom = ChatRoom.builder()
                .chatRoomId(1L)
                .name("테스트 채팅방")
                .image("test.jpg")
                .build();

        testUserChatRoom = UserChatRoom.builder()
                .chatRoomId(1L)
                .userId("user1")
                .build();

        testMessage = Message.builder()
                .messageId("message1")
                .chatRoomId(1L)
                .content("테스트 메시지")
                .type("CHAT")
                .createdAt(LocalDateTime.now())
                .build();

        testChatRoomRead = ChatRoomRead.builder()
                .chatRoomId(1L)
                .userId("user1")
                .lastReadMessageId("message1")
                .unreadCount(0L)
                .build();
    }

    @Test
    @DisplayName("채팅방 목록과 읽지 않은 메시지 수 조회 성공 테스트")
    void getChatRoomsWithUnreadCountSuccess() {
        // given
        String userId = "user1";
        List<UserChatRoom> userChatRooms = Arrays.asList(testUserChatRoom);

        when(userChatRoomRepository.findAllByUserId(userId)).thenReturn(userChatRooms);
        when(chatRoomRepository.findById(anyLong())).thenReturn(Optional.of(testChatRoom));
        when(messageRepository.findByChatRoomIdOrderByCreatedAtAsc(anyLong()))
                .thenReturn(Flux.just(testMessage));
        when(messageRepository.findByChatRoomIdAndTypeNotOrderByCreatedAtAsc(anyLong(), eq("SYSTEM")))
                .thenReturn(Flux.just(testMessage));

        // when
        Mono<List<ChatRoomUnreadDto>> result = chatService.getChatRoomsWithUnreadCount(userId);

        // then
        StepVerifier.create(result)
                .expectNextMatches(chatRooms -> {
                    ChatRoomUnreadDto dto = chatRooms.get(0);
                    return dto.getChatRoomId().equals(1L) &&
                            dto.getName().equals("테스트 채팅방") &&
                            dto.getImage().equals("test.jpg") &&
                            dto.getLastMessage().equals("테스트 메시지");
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("마지막으로 읽은 메시지 업데이트 성공 테스트")
    void updateLastReadMessageSuccess() {
        // given
        Long chatRoomId = 1L;
        String userId = "user1";
        String messageId = "message1";

        when(chatRoomReadRepository.findByChatRoomIdAndUserId(chatRoomId, userId))
                .thenReturn(Optional.of(testChatRoomRead));

        // when
        chatService.updateLastReadMessage(chatRoomId, userId, messageId);

        // then
        verify(chatRoomReadRepository).save(any(ChatRoomRead.class));
    }

    @Test
    @DisplayName("읽지 않은 메시지 수 증가 성공 테스트")
    void incrementUnreadCountSuccess() {
        // given
        Long chatRoomId = 1L;
        String senderId = "user1";
        String receiverId = "user2";

        UserChatRoom receiverChatRoom = UserChatRoom.builder()
                .chatRoomId(chatRoomId)
                .userId(receiverId)
                .build();

        List<UserChatRoom> subscribers = Arrays.asList(testUserChatRoom, receiverChatRoom);

        ChatRoomRead receiverChatRoomRead = ChatRoomRead.builder()
                .chatRoomId(chatRoomId)
                .userId(receiverId)
                .unreadCount(0L)
                .build();

        when(userChatRoomRepository.findAllByChatRoomId(chatRoomId)).thenReturn(subscribers);
        when(chatRoomReadRepository.findByChatRoomIdAndUserId(chatRoomId, receiverId))
                .thenReturn(Optional.of(receiverChatRoomRead));

        // when
        chatService.incrementUnreadCount(chatRoomId, senderId);

        // then
        verify(chatRoomReadRepository).save(argThat(chatRoomRead ->
                chatRoomRead.getUserId().equals(receiverId) &&
                        chatRoomRead.getUnreadCount() == 1L
        ));
    }

    @Test
    @DisplayName("새로운 ChatRoomRead 생성 테스트")
    void createNewChatRoomReadSuccess() {
        // given
        Long chatRoomId = 1L;
        String userId = "user1";
        String messageId = "message1";

        when(chatRoomReadRepository.findByChatRoomIdAndUserId(chatRoomId, userId))
                .thenReturn(Optional.empty());

        // when
        chatService.updateLastReadMessage(chatRoomId, userId, messageId);

        // then
        verify(chatRoomReadRepository).save(argThat(chatRoomRead ->
                chatRoomRead.getChatRoomId().equals(chatRoomId) &&
                        chatRoomRead.getUserId().equals(userId) &&
                        chatRoomRead.getLastReadMessageId().equals(messageId) &&
                        chatRoomRead.getUnreadCount() == 0L
        ));
    }

    @Test
    @DisplayName("구독한 채팅방이 없을 때 빈 결과 반환 테스트")
    void getEmptyChatRoomsSuccess() {
        // given
        String userId = "user1";
        when(userChatRoomRepository.findAllByUserId(userId)).thenReturn(Collections.emptyList());

        // when
        Mono<List<ChatRoomUnreadDto>> result = chatService.getChatRoomsWithUnreadCount(userId);

        // then
        StepVerifier.create(result)
                .verifyComplete();
    }
}