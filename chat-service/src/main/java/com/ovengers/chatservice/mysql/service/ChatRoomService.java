package com.ovengers.chatservice.mysql.service;

import com.ovengers.chatservice.client.UserResponseDto;
import com.ovengers.chatservice.client.UserServiceClient;
import com.ovengers.chatservice.mysql.dto.ChatRoomDto;
import com.ovengers.chatservice.mysql.dto.CompositeChatRoomDto;
import com.ovengers.chatservice.mysql.entity.ChatRoom;
import com.ovengers.chatservice.mysql.entity.Invitation;
import com.ovengers.chatservice.mysql.entity.UserChatRoom;
import com.ovengers.chatservice.mysql.exception.InvalidChatRoomNameException;
import com.ovengers.chatservice.mysql.repository.ChatRoomRepository;
import com.ovengers.chatservice.mysql.repository.InvitationRepository;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Getter
@Transactional
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;
    private final UserChatRoomRepository userChatRoomRepository;
    private final InvitationRepository invitationRepository;
    private final UserServiceClient userServiceClient;

    public UserResponseDto getUserInfo(String userId) {
        UserResponseDto userById = userServiceClient.getUserById(userId);

        if (userById == null) {
            throw new RuntimeException("사용자 정보를 가져오는 데 실패했습니다: " + userId);
        }

        return userById;
    }

    // 유효성 검사 메서드
    private void validateChatRoomName(String name) {
        if (StringUtils.isBlank(name)) { // Apache Commons Lang 사용 (공백 또는 null 확인)
            throw new InvalidChatRoomNameException("채팅방 이름은 공백만으로 지정할 수 없습니다.");
        }
    }

    // ChatRoom 및 UserChatRoom 생성
    public CompositeChatRoomDto createChatRoom(String image, String name, String userId, List<String> userIds) {
        validateChatRoomName(name.trim());

        // 유저 목록을 FeignClient를 통해 확인
        List<UserResponseDto> validUsers = userServiceClient.getUsersByIds(userIds);

        // 유저 ID가 유효한지 확인 (존재하지 않는 유저 ID가 있으면 예외 처리)
        List<String> validUserIds = validUsers.stream()
                .map(UserResponseDto::getUserId)
                .toList();

        for (String user : userIds) {
            if (!validUserIds.contains(user)) {
                throw new IllegalArgumentException("유효하지 않은 유저 ID: " + user);
            }
        }

        ChatRoom chatRoom = ChatRoom.builder()
                .name(name)
                .image(image)
                .creatorId(userId)
                .build();
        ChatRoom savedChatRoom = chatRoomRepository.save(chatRoom); // 엔티티 저장

        UserChatRoom creator = UserChatRoom.builder()
                .chatRoomId(savedChatRoom.getChatRoomId())
                .userId(savedChatRoom.getCreatorId())
                .build();
        userChatRoomRepository.save(creator);

        // 생성자를 제외한 초대 유저 목록 생성
        List<String> inviteeIds = validUserIds.stream()
                .filter(id -> !id.equals(userId)) // 생성자 자신을 제외
                .toList();

        // 초대할 유저에 대한 초대 처리 (Invitation 생성)
        List<Invitation> invitations = inviteeIds.stream()
                .map(getUserId -> Invitation.builder()
                        .chatRoomId(savedChatRoom.getChatRoomId())
                        .userId(getUserId)
                        .accepted(false)
                        .build())
                .toList();
        invitationRepository.saveAll(invitations);

        return CompositeChatRoomDto.builder()
                .chatRoomDto(savedChatRoom.toDto())
                .userChatRoomDto(List.of(creator.toDto())) // 생성자만 포함
                .build();
    }

    // UserId 별 ChatRoomList 조회
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

    // chatRoomId 별 userList 조회
    public List<UserResponseDto> getSubUsers(Long chatRoomId, String userId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new EntityNotFoundException(chatRoomId + "번 채팅방은 존재하지 않습니다."));

        if (!userChatRoomRepository.existsByChatRoomIdAndUserId(chatRoomId, userId)) {
            throw new IllegalArgumentException(chatRoomId + "번 채팅방에 구독되어 있지 않습니다.");
        }

        // UserChatRoom에서 해당 chatRoomId의 유저 리스트 가져오기
        List<String> userIds = userChatRoomRepository.findAllByChatRoomId(chatRoom.getChatRoomId())
                .stream()
                .map(UserChatRoom::getUserId) // userId 추출
                .toList();

        return userIds.stream()
                .map(userServiceClient::getUserById) // UserClient 호출
                .collect(Collectors.toList());
    }

    // 채팅방에 초대
    @Transactional
    public void inviteUsers(Long chatRoomId, String inviterId, List<String> inviteUserIds) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new EntityNotFoundException(chatRoomId + "번 채팅방은 존재하지 않습니다."));

        if (!userChatRoomRepository.existsByChatRoomIdAndUserId(chatRoomId, inviterId)) {
            throw new IllegalArgumentException(chatRoomId + "번 채팅방에 구독되어 있지 않습니다.");
        }

        // 기존 채팅방 유저 목록 조회
        List<String> existingUserIds = userChatRoomRepository.findAllByChatRoomId(chatRoom.getChatRoomId())
                .stream()
                .map(UserChatRoom::getUserId)
                .toList();

        // 초대 유저 목록 중 기존 유저에 포함되지 않은 유저 필터링
        List<String> newUserIds = inviteUserIds.stream()
                .filter(userId -> !existingUserIds.contains(userId))
                .toList();

        if (newUserIds.isEmpty()) {
            throw new IllegalArgumentException("모든 초대 대상 유저가 이미 채팅방에 속해 있습니다.");
        }

        // FeignClient로 초대할 유저들이 유효한지 확인
        List<UserResponseDto> validUsers = userServiceClient.getUsersByIds(newUserIds);
        List<String> validUserIds = validUsers.stream()
                .map(UserResponseDto::getUserId)
                .toList();

        // 초대 대상 중 유효하지 않은 유저 ID가 있다면 예외 처리
        for (String userId : newUserIds) {
            if (!validUserIds.contains(userId)) {
                throw new IllegalArgumentException("존재하지 않는 유저 ID: " + userId);
            }
        }

        // 초대 처리
        List<Invitation> invitations = validUserIds.stream()
                .filter(inviteeId -> {
                    // 이미 초대된 유저인지 확인
                    boolean alreadyInvited = invitationRepository.existsByChatRoomIdAndUserIdAndAcceptedFalse(chatRoomId, inviteeId);
                    if (alreadyInvited) {
                        System.out.println("이미 초대된 유저: " + inviteeId);
                    }
                    return !alreadyInvited;
                })
                .map(inviteeId -> Invitation.builder()
                        .chatRoomId(chatRoom.getChatRoomId())
                        .userId(inviteeId)
                        .accepted(false)
                        .build())
                .toList();

        // 아직 초대만
        invitationRepository.saveAll(invitations);
    }

    // 초대 수락 시 UserChatRoom 저장
    @Transactional
    public void acceptInvitation(Long chatRoomId, String userId) {
        // 초대받은 유저인지 확인
        Invitation invitation = invitationRepository.findByChatRoomIdAndUserId(chatRoomId, userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 채팅방에 초대받은 기록이 없습니다."));

        // 이미 초대를 수락한 경우 예외 처리
        if (invitation.isAccepted()) {
            throw new IllegalArgumentException("이미 초대를 수락했습니다.");
        }

        // 초대 상태를 수락으로 변경
        invitation.setAccepted(true);
        invitationRepository.save(invitation);

        // UserChatRoom에 추가
        UserChatRoom userChatRoom = UserChatRoom.builder()
                .chatRoomId(chatRoomId)
                .userId(userId)
                .build();
        userChatRoomRepository.save(userChatRoom);
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

    // 채팅방 생성자만 채팅방 삭제 및 해당 채팅방 구독자 전체 삭제
    public void deleteChatRoom(Long chatRoomId, String userId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new EntityNotFoundException(chatRoomId + "번 채팅방은 존재하지 않습니다."));

        if (!chatRoom.getCreatorId().equals(userId)) {
            throw new SecurityException("채팅방 삭제 권한이 없습니다.");
        }

        chatRoomRepository.delete(chatRoom);
        userChatRoomRepository.deleteByChatRoomId(chatRoom.getChatRoomId());
        // 초대 기록 삭제
        invitationRepository.deleteByChatRoomId(chatRoom.getChatRoomId());
    }

    // 채팅방 나가기(채팅방 생성자는 불가능)
    public void disconnectChatRoom(Long chatRoomId, String userId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new EntityNotFoundException(chatRoomId + "번 채팅방은 존재하지 않습니다."));

        if (!userChatRoomRepository.existsByChatRoomIdAndUserId(chatRoomId, userId)) {
            throw new IllegalArgumentException(chatRoomId + "번 채팅방에 구독되어 있지 않습니다.");
        }

        if (chatRoom.getCreatorId().equals(userId)) {
            throw new SecurityException("채팅방 생성자는 채팅방을 나갈 수 없습니다.");
        }

        userChatRoomRepository.deleteByChatRoomIdAndUserId(chatRoomId, userId);

        // 초대 기록 삭제
        invitationRepository.findByChatRoomIdAndUserId(chatRoomId, userId)
                .ifPresent(invitationRepository::delete);
    }

    @Transactional
    public void removeUserFromChatRoom(Long chatRoomId, String userIdToRemove, String userId) {
        // 채팅방 존재 여부 확인
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new EntityNotFoundException(chatRoomId + "번 채팅방은 존재하지 않습니다."));

        // 요청자가 채팅방 생성자인지 확인
        if (!chatRoom.getCreatorId().equals(userId)) {
            throw new IllegalArgumentException("채팅방 생성자만 사용자를 내보낼 수 있습니다.");
        }

        // 내보낼 유저가 채팅방에 속해 있는지 확인
        UserChatRoom userChatRoom = userChatRoomRepository.findByChatRoomIdAndUserId(chatRoomId, userIdToRemove)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저는 채팅방에 속해 있지 않습니다."));

        // 생성자를 내보내려고 하는 경우 예외 처리
        if (chatRoom.getCreatorId().equals(userIdToRemove)) {
            throw new IllegalArgumentException("채팅방 생성자는 내보낼 수 없습니다.");
        }

        // 유저 내보내기 (UserChatRoom 삭제)
        userChatRoomRepository.delete(userChatRoom);

        // 초대 기록 삭제
        invitationRepository.findByChatRoomIdAndUserId(chatRoomId, userIdToRemove)
                .ifPresent(invitationRepository::delete);
    }

//    public String cleanInput(String input) {
//        if (input == null) {
//            return null;
//        }
//        // 문자열 양 끝의 쌍따옴표만 제거
//        return input.startsWith("\"") && input.endsWith("\"")
//                ? input.substring(1, input.length() - 1)
//                : input;
//    }

}
