package com.ovengers.chatservice.mysql.service;

import com.ovengers.chatservice.common.auth.TokenUserInfo;
import com.ovengers.chatservice.mysql.dto.ChatRoomDto;
import com.ovengers.chatservice.mysql.entity.ChatRoom;
import com.ovengers.chatservice.mysql.entity.UserChatRoom;
import com.ovengers.chatservice.mysql.exception.InvalidChatRoomNameException;
import com.ovengers.chatservice.mysql.repository.ChatRoomRepository;
import com.ovengers.chatservice.mysql.repository.UserChatRoomRepository;
import io.micrometer.common.util.StringUtils;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Getter
@Transactional
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;
    private final UserChatRoomRepository userChatRoomRepository;

    public String cleanInput(String input) {
        if (input == null) {
            return null;
        }
        // 문자열 양 끝의 쌍따옴표만 제거
        return input.startsWith("\"") && input.endsWith("\"")
                ? input.substring(1, input.length() - 1)
                : input;
    }

    // 채팅방 리스트 조회
    public List<ChatRoomDto> getAllChatRooms(String userId) {
        // 사용자가 구독 중인 채팅방 ID 목록 가져오기
        List<Long> subscribedChatRoomIds = userChatRoomRepository.findAllByUserId(userId)
                .stream()
                .map(UserChatRoom::getChatRoomId)
                .toList();

        if (subscribedChatRoomIds.isEmpty()) {
            return Collections.emptyList(); // 빈 리스트 반환
        }

        // 구독 중인 채팅방만 조회
        return chatRoomRepository.findAllById(subscribedChatRoomIds)
                .stream()
                .map(ChatRoomDto::fromEntity) // 엔티티를 DTO로 변환
                .toList();
    }

    // 채팅방 생성
    public ChatRoomDto createChatRoom(String name, TokenUserInfo tokenUserInfo) {

        // 입력값 정제
        String cleanedName = cleanInput(name);

        // 유효성 검사
        validateChatRoomName(cleanedName);
        
        ChatRoom chatRoom = ChatRoom.builder()
                .name(cleanedName.trim())
                .creatorId(tokenUserInfo.getId())
                .build();

        ChatRoom savedChatRoom = chatRoomRepository.save(chatRoom);

        return ChatRoomDto.fromEntity(savedChatRoom);
    }

    // 채팅방 이름 수정
    public ChatRoomDto updateChatRoom(Long chatRoomId, String newName, TokenUserInfo tokenUserInfo) {

        // 입력값 정제
        String cleanedName = cleanInput(newName);

        // 유효성 검사
        validateChatRoomName(cleanedName);

        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new EntityNotFoundException(chatRoomId + "번 채팅방은 존재하지 않습니다."));

        // 채팅방 생성자만 수정 가능하도록 검증
        if (!chatRoom.getCreatorId().equals(tokenUserInfo.getId())) {
            throw new SecurityException("채팅방 수정 권한이 없습니다.");
        }

        chatRoom.setName(cleanedName.trim()); // 이름 양끝 공백 제거
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
