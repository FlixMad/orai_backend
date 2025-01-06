package com.ovengers.chatservice.mysql.service;

import com.ovengers.chatservice.mysql.dto.ChatRoomDto;
import com.ovengers.chatservice.mysql.entity.ChatRoom;
import com.ovengers.chatservice.mysql.exception.InvalidChatRoomNameException;
import com.ovengers.chatservice.mysql.repository.ChatRoomRepository;
import io.micrometer.common.util.StringUtils;
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
        validateChatRoomName(name); // 유효성 검사 추가
        ChatRoom chatRoom = ChatRoom.builder()
                .name(name.trim()) // 이름 양끝 공백 제거
                .createdAt(LocalDateTime.now())
                .build();
        return ChatRoomDto.fromEntity(chatRoomRepository.save(chatRoom));
    }

    // 채팅방 이름 수정
    public ChatRoomDto updateChatRoom(Long chatRoomId, String newName) {
        validateChatRoomName(newName); // 유효성 검사 추가
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new EntityNotFoundException(chatRoomId + "번 채팅방은 존재하지 않습니다."));
        chatRoom.setName(newName.trim()); // 이름 양끝 공백 제거
        return ChatRoomDto.fromEntity(chatRoomRepository.save(chatRoom));
    }

    // 유효성 검사 메서드
    private void validateChatRoomName(String name) {
        if (StringUtils.isBlank(name)) { // Apache Commons Lang 사용 (공백 또는 null 확인)
            throw new InvalidChatRoomNameException("채팅방 이름은 공백만으로 지정할 수 없습니다.");
        }
    }

    // 채팅방 삭제
    public void deleteChatRoom(Long chatRoomId) {
        chatRoomRepository.deleteById(chatRoomId);
    }

}
