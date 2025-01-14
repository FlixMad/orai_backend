package com.ovengers.chatservice.mysql.service;

import com.ovengers.chatservice.client.UserResponseDto;
import com.ovengers.chatservice.client.UserServiceClient;
import com.ovengers.chatservice.common.auth.TokenUserInfo;
import com.ovengers.chatservice.common.dto.CommonResDto;
import com.ovengers.chatservice.mysql.dto.ChatRoomDto;
import com.ovengers.chatservice.mysql.dto.UserChatRoomDto;
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
    private final UserChatRoomService userChatRoomService;
    private final UserServiceClient userServiceClient;

    private void validateInvitedUsers(List<String> inviteUserIds) {
        inviteUserIds.forEach(userId -> {
            CommonResDto<UserResponseDto> response = userServiceClient.getUser(userId);
            if (response.getResult() == null) {
                throw new IllegalArgumentException("초대된 사용자 정보가 유효하지 않습니다: " + userId);
            }
        });
    }

    public String cleanInput(String input) {
        if (input == null) {
            return null;
        }
        // 문자열 양 끝의 쌍따옴표만 제거
        return input.startsWith("\"") && input.endsWith("\"")
                ? input.substring(1, input.length() - 1)
                : input;
    }

    // 유효성 검사 메서드
    private void validateChatRoomName(String name) {
        if (StringUtils.isBlank(name)) { // Apache Commons Lang 사용 (공백 또는 null 확인)
            throw new InvalidChatRoomNameException("채팅방 이름은 공백만으로 지정할 수 없습니다.");
        }
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
    public ChatRoomDto createChatRoomWithInvites(String name, String image, List<String> inviteUserIds, TokenUserInfo tokenUserInfo) {
        // 입력값 정제 및 유효성 검사
        String cleanedName = cleanInput(name);
        validateChatRoomName(cleanedName);
        validateInvitedUsers(inviteUserIds);

        // 채팅방 생성
        ChatRoom chatRoom = ChatRoom.builder()
                .name(cleanedName.trim())
                .image(image)
                .creatorId(tokenUserInfo.getId())
                .build();
        ChatRoom savedChatRoom = chatRoomRepository.save(chatRoom);

        // 초대된 사용자 처리
        inviteUserIds.add(tokenUserInfo.getId()); // 방 생성자도 구독하도록 추가
        inviteUserIds.forEach(userId -> userChatRoomService.subscribeToChatRoom(
                UserChatRoomDto.builder()
                        .chatRoomId(savedChatRoom.getChatRoomId())
                        .userId(userId)
                        .build()
        ));

        return ChatRoomDto.fromEntity(savedChatRoom);
    }

    // 채팅방 이름 수정
    public ChatRoomDto updateChatRoomName(Long chatRoomId, String newName, TokenUserInfo tokenUserInfo) {

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

    // 채팅방 이미지 수정
    public ChatRoomDto updateChatRoomImage(Long chatRoomId, String newImage, TokenUserInfo tokenUserInfo) {

        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new EntityNotFoundException(chatRoomId + "번 채팅방은 존재하지 않습니다."));

        // 채팅방 생성자만 수정 가능하도록 검증
        if (!chatRoom.getCreatorId().equals(tokenUserInfo.getId())) {
            throw new SecurityException("채팅방 수정 권한이 없습니다.");
        }

        chatRoom.setImage(newImage); // 이름 양끝 공백 제거
        return ChatRoomDto.fromEntity(chatRoomRepository.save(chatRoom));
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
