package com.ovengers.chatservice.mysql.service;

import com.ovengers.chatservice.common.auth.TokenUserInfo;
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
    public List<ChatRoomDto> getAllChatRooms(String creatorId) {
        log.info("채팅방 조회 요청 처리 중 - 사용자 ID: {}", creatorId);
        List<ChatRoomDto> list = chatRoomRepository.findAllByCreatorIdOrderByCreatedAtDesc(creatorId)
                .stream()
                .map(ChatRoomDto::fromEntity) // 엔티티를 DTO로 변환
                .toList();
        log.info("채팅방 조회 완료 - 사용자 ID: {}, 채팅방 개수: {}", creatorId, list.size());
        return list;
    }

    // 채팅방 생성
    public ChatRoomDto createChatRoom(String name, TokenUserInfo tokenUserInfo) {
        validateChatRoomName(name); // 유효성 검사 추가
        log.info("채팅방 생성 요청 - 사용자 ID: {}", tokenUserInfo.getId());
        ChatRoom chatRoom = ChatRoom.builder()
                .name(name.trim()) // 이름 양끝 공백 제거
                .createdAt(LocalDateTime.now())
                .creatorId(tokenUserInfo.getId())
                .build();
        return ChatRoomDto.fromEntity(chatRoomRepository.save(chatRoom));
    }

    // 채팅방 이름 수정
    public ChatRoomDto updateChatRoom(Long chatRoomId, String newName, TokenUserInfo tokenUserInfo) {
        validateChatRoomName(newName); // 유효성 검사 추가
        log.info("채팅방 수정 요청 - 사용자 ID: {}", tokenUserInfo.getId());

        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new EntityNotFoundException(chatRoomId + "번 채팅방은 존재하지 않습니다."));

        // 채팅방 생성자만 수정 가능하도록 검증
        if (!chatRoom.getCreatorId().equals(tokenUserInfo.getId())) {
            throw new SecurityException("채팅방 수정 권한이 없습니다.");
        }

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
    public void deleteChatRoom(Long chatRoomId, TokenUserInfo userInfo) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new EntityNotFoundException(chatRoomId + "번 채팅방은 존재하지 않습니다."));

        // 채팅방 생성자만 삭제 가능하도록 검증
        if (!chatRoom.getCreatorId().equals(userInfo.getId())) {
            throw new SecurityException("채팅방 삭제 권한이 없습니다.");
        }

        log.info("채팅방 삭제 요청 - 사용자 ID: {}, 채팅방 ID: {}", userInfo.getId(), chatRoomId);
        chatRoomRepository.deleteById(chatRoomId);
    }

}
