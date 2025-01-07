package com.ovengers.chatservice.mysql.service;

import com.ovengers.chatservice.mysql.dto.UnreadMessageDto;
import com.ovengers.chatservice.mysql.entity.UnreadMessage;
import com.ovengers.chatservice.mysql.repository.UnreadMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UnreadMessageService {


    private final UnreadMessageRepository unreadMessageRepository;

    // 읽지 않은 메시지 수 증가
    public void incrementUnreadCount(Long chatRoomId, String userId) {
        unreadMessageRepository.incrementUnreadCountForOtherUsers(chatRoomId, userId);
    }

    // 특정 사용자의 읽지 않은 메시지 수 조회
    public UnreadMessageDto getUnreadCount(Long chatRoomId, String userId) {
        return unreadMessageRepository.findByChatRoomIdAndUserId(chatRoomId, userId)
                .map(this::toDto)
                .orElseGet(() -> UnreadMessageDto.builder()
                        .chatRoomId(chatRoomId)
                        .userId(userId)
                        .unreadCount(0L)
                        .build());
    }

    // 읽지 않은 메시지 수 초기화
    public void resetUnreadCount(Long chatRoomId, String userId) {
        unreadMessageRepository.resetUnreadCount(chatRoomId, userId);
    }

    // 특정 채팅방의 모든 읽지 않은 메시지 수 조회
    public List<UnreadMessageDto> getUnreadCountsByChatRoom(Long chatRoomId) {
        return unreadMessageRepository.findByChatRoomId(chatRoomId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    // 채팅방 생성 시 사용자별 초기화
    public void initializeUnreadCountsForChatRoom(Long chatRoomId, List<String> userIds) {
        List<UnreadMessage> unreadMessages = userIds.stream()
                .map(userId -> UnreadMessage.builder()
                        .chatRoomId(chatRoomId)
                        .userId(userId)
                        .unreadCount(0L)
                        .build())
                .toList();
        unreadMessageRepository.saveAll(unreadMessages);
    }

    // 엔티티 -> DTO 변환 메서드
    private UnreadMessageDto toDto(UnreadMessage entity) {
        return UnreadMessageDto.builder()
                .chatRoomId(entity.getChatRoomId())
                .userId(String.valueOf(entity.getUserId()))
                .unreadCount(entity.getUnreadCount())
                .build();
    }
}
