package com.ovengers.chatservice.mysql.service;

import com.ovengers.chatservice.mysql.dto.ChatRoomDto;
import com.ovengers.chatservice.mysql.entity.ChatRoom;
import com.ovengers.chatservice.mysql.repository.ChatRoomRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Getter
@Transactional
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;

    // 채팅방 리스트 조회
    public List<ChatRoomDto> getAllChatRooms() {
        return chatRoomRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(ChatRoomDto::fromEntity) // 엔티티를 DTO로 변환
                .toList();
    }

    // 채팅방 생성
    public ChatRoomDto createChatRoom(String name) {
        ChatRoom chatRoom = ChatRoom.builder()
                .name(name)
                .createdAt(LocalDateTime.now())
                .build();
        return ChatRoomDto.fromEntity(chatRoomRepository.save(chatRoom));
    }

    // 채팅방 이름 수정
    public ChatRoomDto updateChatRoom(Long chatRoomId, String newName) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new EntityNotFoundException("ChatRoom not found with id: " + chatRoomId));
        chatRoom.setName(newName);
        return ChatRoomDto.fromEntity(chatRoomRepository.save(chatRoom));
    }

    // 채팅방 삭제
    public void deleteChatRoom(Long chatRoomId) {
        chatRoomRepository.deleteById(chatRoomId);
    }

}
