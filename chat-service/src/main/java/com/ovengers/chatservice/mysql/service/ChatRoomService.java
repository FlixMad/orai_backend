package com.ovengers.chatservice.mysql.service;

import com.ovengers.chatservice.client.UserResponseDto;
import com.ovengers.chatservice.client.UserServiceClient;
import com.ovengers.chatservice.common.auth.TokenUserInfo;
import com.ovengers.chatservice.common.dto.CommonResDto;
import com.ovengers.chatservice.mysql.dto.ChatRoomDto;
import com.ovengers.chatservice.mysql.dto.ChatRoomRequestDto;
import com.ovengers.chatservice.mysql.dto.CompositeChatRoomDto;
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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Getter
@Transactional
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;
    private final UserChatRoomRepository userChatRoomRepository;
    private final UserServiceClient userServiceClient;

    public UserResponseDto getUserProfile(String userId) {
        // Feign 클라이언트를 통해 사용자 정보 조회
        CommonResDto<?> response = userServiceClient.getUser(userId);

        // 성공 여부 검증 및 데이터 반환
        if (response == null || response.getStatusCode() != HttpStatus.OK.value() || response.getResult() == null) {
            throw new RuntimeException("사용자 정보를 가져오는 데 실패했습니다: " + userId);
        }

        // 결과를 UserResponseDto로 변환
        return (UserResponseDto) response.getResult();
    }

    // 유효성 검사 메서드
    private void validateChatRoomName(String name) {
        if (StringUtils.isBlank(name)) { // Apache Commons Lang 사용 (공백 또는 null 확인)
            throw new InvalidChatRoomNameException("채팅방 이름은 공백만으로 지정할 수 없습니다.");
        }
    }

    // ChatRoom 및 UserChatRoom 생성
    public CompositeChatRoomDto createChatRoom(String image, String name, String userId) {
        validateChatRoomName(name.trim());

        ChatRoom chatRoom = ChatRoom.builder()
                .name(name)
                .image(image)
                .creatorId(userId)
                .build();
        ChatRoom savedChatRoom = chatRoomRepository.save(chatRoom); // 엔티티 저장

        UserChatRoom userChatRoom = UserChatRoom.builder()
                .chatRoomId(savedChatRoom.getChatRoomId())
                .userId(savedChatRoom.getCreatorId())
                .build();
        UserChatRoom savedUserChatRoom = userChatRoomRepository.save(userChatRoom);

        return CompositeChatRoomDto.builder()
                .chatRoomDto(savedChatRoom.toDto())
                .userChatRoomDto(savedUserChatRoom.toDto())
                .build();
    }

    // UserId 별 ChatRoom 조회
    public List<ChatRoomDto> getChatRooms(String userId) {
        // UserChatRoom에서 해당 사용자의 ChatRoomId 리스트 가져오기
        List<Long> chatRoomIds = userChatRoomRepository.findAllByUserId(userId)
                .stream()
                .map(UserChatRoom::getChatRoomId) // chatRoomId 추출
                .collect(Collectors.toList());

        // ChatRoomId가 없을 경우 빈 리스트 반환
        if (chatRoomIds.isEmpty()) {
            return Collections.emptyList();
        }

        // ChatRoomId에 해당하는 ChatRoom 엔티티를 조회하고 DTO로 변환
        return chatRoomRepository.findAllById(chatRoomIds)
                .stream()
                .map(ChatRoom::toDto)
                .collect(Collectors.toList());
    }

    // 채팅방 생성자만 채팅방 이미지 및 이름 수정
    public ChatRoomDto updateChatRoom(Long chatRoomId, String newImage, String newName, String userId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new EntityNotFoundException(chatRoomId + "번 채팅방은 존재하지 않습니다."));

        // 채팅방 생성자만 수정 가능하도록 검증
        if (!chatRoom.getCreatorId().equals(userId)) {
            throw new SecurityException("채팅방 수정 권한이 없습니다.");
        }

        boolean isUpdated = false;

        // 이미지가 null이 아니고 공백이 아닌 경우 수정
        if (newImage != null && !newImage.trim().isEmpty() && !newImage.equals(chatRoom.getImage())) {
            chatRoom.setImage(newImage);
            isUpdated = true;
        }

        // 이름이 null이 아니고 공백이 아닌 경우 수정
        if (newName != null && !newName.trim().isEmpty() && !newName.equals(chatRoom.getName())) {
            validateChatRoomName(newName.trim());
            chatRoom.setName(newName);
            isUpdated = true;
        }

        // 둘 다 수정되지 않은 경우 예외 발생
        if (!isUpdated) {
            throw new IllegalArgumentException("변경된 이미지나 이름이 없습니다.");
        }

        return chatRoom.toDto();
    }

    // 채팅방 생성자만 채팅방 삭제 및 해당 채팅방 구독자 삭제
    public void deleteChatRoom(Long chatRoomId, String userId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new EntityNotFoundException(chatRoomId + "번 채팅방은 존재하지 않습니다."));

        if (!chatRoom.getCreatorId().equals(userId)) {
            throw new SecurityException("채팅방 삭제 권한이 없습니다.");
        }

        chatRoomRepository.delete(chatRoom);
        userChatRoomRepository.deleteByChatRoomId(chatRoom.getChatRoomId());
    }


//    private void validateInvitedUsers(List<String> inviteUserIds) {
//        inviteUserIds.forEach(userId -> {
//            CommonResDto<UserResponseDto> response = userServiceClient.getUser(userId);
//            if (response.getResult() == null) {
//                throw new IllegalArgumentException("초대된 사용자 정보가 유효하지 않습니다: " + userId);
//            }
//        });
//    }
//
//    public String cleanInput(String input) {
//        if (input == null) {
//            return null;
//        }
//        // 문자열 양 끝의 쌍따옴표만 제거
//        return input.startsWith("\"") && input.endsWith("\"")
//                ? input.substring(1, input.length() - 1)
//                : input;
//    }
//

//
//    // 채팅방 생성
//    public ChatRoomDto createChatRoomWithInvites(String name, String image, List<String> inviteUserIds, TokenUserInfo tokenUserInfo) {
//        // 입력값 정제 및 유효성 검사
//        String cleanedName = cleanInput(name);
//        validateChatRoomName(cleanedName);
//        validateInvitedUsers(inviteUserIds);
//
//        // 채팅방 생성
//        ChatRoom chatRoom = ChatRoom.builder()
//                .name(cleanedName.trim())
//                .image(image)
//                .creatorId(tokenUserInfo.getId())
//                .build();
//        ChatRoom savedChatRoom = chatRoomRepository.save(chatRoom);
//
//        // 초대된 사용자 처리
//        inviteUserIds.add(tokenUserInfo.getId()); // 방 생성자도 구독하도록 추가
//        inviteUserIds.forEach(userId -> userChatRoomService.subscribeToChatRoom(
//                UserChatRoomDto.builder()
//                        .chatRoomId(savedChatRoom.getChatRoomId())
//                        .userId(userId)
//                        .build()
//        ));
//
//        return ChatRoomDto.fromEntity(savedChatRoom);
//    }


}
