package com.ovengers.chatservice.mysql.service;

import com.ovengers.chatservice.client.UserResponseDto;
import com.ovengers.chatservice.client.UserServiceClient;
import com.ovengers.chatservice.mysql.dto.ChatRoomDto;
import com.ovengers.chatservice.mysql.dto.ChatRoomInvitationDto;
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
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
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
    private final SimpMessagingTemplate simpMessagingTemplate;

    public UserResponseDto getUserInfo(String userId) {
        UserResponseDto userById = userServiceClient.getUserById(userId);

        if (userById == null) {
            throw new RuntimeException("사용자 정보를 가져오는 데 실패했습니다: " + userId);
        }

        return userById;
    }

    public List<UserResponseDto> getAllUsers(List<String> userIds) {
        return userServiceClient.getUsersByIds(userIds);
    }

    // 유효성 검사 메서드
    private void validateChatRoomName(String name) {
        if (StringUtils.isBlank(name)) { // Apache Commons Lang 사용 (공백 또는 null 확인)
            throw new InvalidChatRoomNameException("채팅방 이름은 공백만으로 지정할 수 없습니다.");
        }
    }

/*    // 생성자에게 채팅방 생성 알림
    private void sendChatRoomCreatedNotification(String userId, ChatRoom chatRoom) {
        // STOMP 메시지를 생성자에게 전송
        simpMessagingTemplate.convertAndSendToUser(
                userId,                // 대상 사용자
                "/queue/chat-room",    // 개인 큐 경로
                ChatRoomNotificationDto.builder()
                        .chatRoomId(chatRoom.getChatRoomId())
                        .name(chatRoom.getName())
                        .message(chatRoom.getName() + "채팅방이 생성되었습니다.")
                        .build()
        );
    }*/

/*    // 초대 유저들에게 알림을 보내는 메서드
    private void sendInvitationsToUsers(List<String> userIds, ChatRoom chatRoom, String inviterId) {
        // 초대한 사람의 정보를 가져옴
        UserResponseDto inviterInfo = getUserInfo(inviterId);
        String inviterName = inviterInfo.getName(); // 초대한 사람의 이름 추출

        // 초대받은 사용자들에게 초대 메시지 전송
        userIds.forEach(userId -> simpMessagingTemplate.convertAndSendToUser(
                userId,                // 대상 사용자
                "/queue/invitations",  // 개인 큐 경로
                ChatRoomInvitationDto.builder()
                        .chatRoomId(chatRoom.getChatRoomId())
                        .name(chatRoom.getName())
                        .message(inviterName + "님이 " + chatRoom.getName() + " 채팅방에 초대했습니다.")
                        .build()
        ));
    }*/

    // 채팅방 입장 알림(채팅방 생성 시(생성자))
    private void sendEnterChatRoom(Long chatRoomId, String userId) {
        // 유저의 이름 가져오기
        UserResponseDto userInfo = getUserInfo(userId);
        String userName = userInfo.getName(); // 유저의 이름

        ChatRoomDto chatRoomInfo = chatRoomRepository.findByChatRoomId(chatRoomId).toDto();

        // 채팅방에 속한 모든 유저에게 메시지 전송
        simpMessagingTemplate.convertAndSend(
                "/sub/" + chatRoomId + "/chat",  // 채팅방 구독 경로
                userName + "님이 " + chatRoomInfo.getName() + " 채팅방에 입장했습니다."
        );
    }

    // 채팅방 입장 알림(채팅방 생성 또는 초대 시(유저들))
    private void sendEnterUsers(Long chatRoomId, List<String> userIds) {
        ChatRoomDto chatRoomInfo = chatRoomRepository.findByChatRoomId(chatRoomId).toDto();
        List<UserResponseDto> userInfos = getAllUsers(userIds);

        // 한 번의 메시지로 모든 입장 사용자 표시
        String userNames = userInfos.stream()
                .map(UserResponseDto::getName)
                .collect(Collectors.joining(", "));

        simpMessagingTemplate.convertAndSend("/sub/" + chatRoomId + "/chat", userNames + "님이 채팅방에 입장했습니다.");
    }

    // 채팅방 수정 알림
    private void sendChatRoomUpdatedNotification(Long chatRoomId, String newImage, String newName) {
        // 채팅방에 속한 유저들이 아닌, 해당 채팅방을 구독한 유저들에게만 알림을 전송
        // "/sub/{chatRoomId}/chat"으로 구독한 유저에게 메시지를 전송
        ChatRoom chatRoom = chatRoomRepository.findByChatRoomId(chatRoomId);

        // 이름이 변경되었을 때
        if (!newName.equals(chatRoom.getName())) {
            String nameUpdateMessage = chatRoom.getName() + "에서 " + newName + "으로 변경되었습니다.";
            simpMessagingTemplate.convertAndSend(
                    "/sub/" + chatRoomId + "/chat",  // 채팅방 구독 경로
                    nameUpdateMessage
            );
        }

        // 이미지가 변경되었을 때
        if (!newImage.equals(chatRoom.getImage())) {
            String imageUpdateMessage = "이미지가 변경되었습니다.";
            simpMessagingTemplate.convertAndSend(
                    "/sub/" + chatRoomId + "/chat",  // 채팅방 구독 경로
                    imageUpdateMessage
            );
        }
    }

    // 채팅방 퇴장 알림(채팅방 나가기)
    private void sendExitChatRoom(Long chatRoomId, String userId) {
        // 유저의 이름 가져오기
        UserResponseDto userInfo = getUserInfo(userId);
        String userName = userInfo.getName(); // 유저의 이름

        ChatRoomDto chatRoomInfo = chatRoomRepository.findByChatRoomId(chatRoomId).toDto();

        // 채팅방에 속한 모든 유저에게 메시지 전송
        simpMessagingTemplate.convertAndSend(
                "/sub/" + chatRoomId + "/chat",  // 채팅방 구독 경로
                userName + "님이 " + chatRoomInfo.getName() + " 채팅방에서 퇴장했습니다."
        );
    }

    // 채팅방 강퇴 알림(채팅방 내보내기)
    private void sendExportChatRoom(Long chatRoomId, String userId, String creatorId) {
        // 유저의 이름 가져오기
        UserResponseDto userInfo = getUserInfo(userId);
        String userName = userInfo.getName();

        // 생성자 이름 가져오기
        UserResponseDto creatorInfo = getUserInfo(creatorId);
        String creatorName = creatorInfo.getName();

        ChatRoomDto chatRoomInfo = chatRoomRepository.findByChatRoomId(chatRoomId).toDto();

        // 채팅방에 속한 모든 유저에게 메시지 전송
        simpMessagingTemplate.convertAndSend(
                "/sub/" + chatRoomId + "/chat",  // 채팅방 구독 경로
                creatorName + "님이 " + userName + "님을 " + chatRoomInfo.getName() + " 채팅방에서 내보냈습니다."
        );
    }


    // 구독 유저들에게 채팅방 삭제 알림을 보내는 메서드
    private void sendChatRoomDeletedNotification(List<String> userIds, ChatRoom chatRoom, String removerId) {
        UserResponseDto removerInfo = getUserInfo(removerId);
        String removerName = removerInfo.getName();

        userIds.forEach(userId -> simpMessagingTemplate.convertAndSendToUser(
                userId,
                "/queue",
                ChatRoomInvitationDto.builder()
                        .chatRoomId(chatRoom.getChatRoomId())
                        .name(chatRoom.getName())
                        .message(removerName + "님이 " + chatRoom.getName() + " 채팅방을 삭제했습니다.")
                        .build()
        ));
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
                        .inviterId(savedChatRoom.getCreatorId())
                        .chatRoomId(savedChatRoom.getChatRoomId())
                        .userId(getUserId)
                        .accepted(true)
                        .build())
                .toList();
        invitationRepository.saveAll(invitations);

        List<UserChatRoom> users = inviteeIds.stream()
                .map(getUserId -> UserChatRoom.builder()
                        .chatRoomId(savedChatRoom.getChatRoomId())
                        .userId(getUserId)
                        .build())
                .toList();
        userChatRoomRepository.saveAll(users);

/*        // **WebSocket 알림 처리 추가**
        sendChatRoomCreatedNotification(userId, savedChatRoom); // 생성자에게 알림
        sendInvitationsToUsers(inviteeIds, chatRoom, userId); // 초대 유저들에게 알림*/

        // 생성자가 채팅방에 입장했을 때 입장 메시지 전송
        sendEnterChatRoom(savedChatRoom.getChatRoomId(), userId);  // 생성자가 입장했음을 알림
        sendEnterUsers(savedChatRoom.getChatRoomId(), inviteeIds);

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

    public ChatRoomDto getChatRoom(Long chatRoomId, String userId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new EntityNotFoundException(chatRoomId + "번 채팅방은 존재하지 않습니다."));

        if (!userChatRoomRepository.existsByChatRoomIdAndUserId(chatRoomId, userId)) {
            throw new IllegalArgumentException(chatRoomId + "번 채팅방에 구독되어 있지 않습니다.");
        }

        return chatRoom.toDto();
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

        if (inviteUserIds == null || inviteUserIds.isEmpty()) {
            throw new IllegalArgumentException("초대할 사용자 목록이 비어있습니다.");
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
                        .inviterId(inviterId)
                        .chatRoomId(chatRoom.getChatRoomId())
                        .userId(inviteeId)
                        .accepted(true)
                        .build())
                .toList();

        List<UserChatRoom> users = validUserIds.stream()
                .map(getUserId -> UserChatRoom.builder()
                        .chatRoomId(chatRoom.getChatRoomId())
                        .userId(getUserId)
                        .build())
                .toList();

        // 초대와 구독
        // 동시성 처리를 위한 락 획득
        Lock lock = new ReentrantLock();
        lock.lock();
        try {
            invitationRepository.saveAll(invitations);
            userChatRoomRepository.saveAll(users);
            sendEnterUsers(chatRoomId, validUserIds);
        } finally {
            lock.unlock();
        }
//        sendInvitationsToUsers(validUserIds, chatRoom, inviterId);       // 초대 유저들에게 알림
    }

/*    // 초대 수락 시 UserChatRoom 저장
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

        sendEnterChatRoom(chatRoomId, userId);  // 유저가 입장했음을 알림
    }*/

/*    // 초대 거절 시 Invitation 삭제
    @Transactional
    public void refusalInvitation(Long chatRoomId, String userId) {
        // 초대받은 유저인지 확인
        Invitation invitation = invitationRepository.findByChatRoomIdAndUserId(chatRoomId, userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 채팅방에 초대받은 기록이 없습니다."));

        // 이미 초대를 수락한 경우 예외 처리
        if (invitation.isAccepted()) {
            throw new IllegalArgumentException("이미 초대를 수락했습니다.");
        }

        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채팅방입니다."));

        String inviterId = invitation.getInviterId();

        UserResponseDto inviterInfo = getUserInfo(userId);
        String refusalName = inviterInfo.getName(); // 초대를 거절한 유저의 이름

        // 초대 거절 알림 메시지 전송
        simpMessagingTemplate.convertAndSendToUser(
                inviterId,                // 초대한 유저에게 보냄
                "/queue/notifications",  // 개인 큐 경로
                "사용자 " + refusalName + "님이 " + chatRoom.getName() + " 채팅방 초대를 거절했습니다."
        );

        invitationRepository.deleteByChatRoomIdAndUserId(chatRoomId, userId);
    }*/

    // 채팅방 생성자만 채팅방 이미지 및 이름 수정
    public ChatRoomDto updateChatRoom(Long chatRoomId, String newImage, String newName, String userId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new EntityNotFoundException(chatRoomId + "번 채팅방은 존재하지 않습니다."));

        // 채팅방 생성자만 수정 가능하도록 검증
        if (!chatRoom.getCreatorId().equals(userId)) {
            throw new SecurityException("채팅방 수정 권한이 없습니다.");
        }

        boolean isUpdated = false;

        // 이미지가 공백이 아닌 경우 수정
        if (!newImage.trim().isEmpty() && !newImage.equals(chatRoom.getImage())) {
            chatRoom.setImage(newImage);
            isUpdated = true;
        }

        log.info("\n\n\n{}\n\n\n", newImage);

        // 이름이 null이 아니고 공백이 아닌 경우 수정
        if (newName != null && !newName.trim().isEmpty() && !newName.equals(chatRoom.getName())) {
            validateChatRoomName(newName.trim());
            chatRoom.setName(newName);
            isUpdated = true;
        }

        log.info("\n\n\n{}\n\n\n", newName);

        // 둘 다 수정되지 않은 경우 예외 발생
        if (!isUpdated) {
            throw new IllegalArgumentException("변경된 이미지나 이름이 없습니다.");
        }

        // 수정된 내용에 대해 해당 채팅방에 알림 보내기
        sendChatRoomUpdatedNotification(chatRoomId, newImage, Objects.requireNonNull(newName));

        return chatRoom.toDto();
    }

    // 채팅방 생성자만 채팅방 삭제 및 해당 채팅방 구독자 전체 삭제
    public void deleteChatRoom(Long chatRoomId, String userId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new EntityNotFoundException(chatRoomId + "번 채팅방은 존재하지 않습니다."));

        if (!chatRoom.getCreatorId().equals(userId)) {
            throw new SecurityException("채팅방 삭제 권한이 없습니다.");
        }

        List<String> existingUserIds = userChatRoomRepository.findAllByChatRoomId(chatRoom.getChatRoomId())
                .stream()
                .map(UserChatRoom::getUserId)
                .toList();

        // 채팅방 삭제 전 구독자에게 알림 전송
        sendChatRoomDeletedNotification(existingUserIds, chatRoom, userId);

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

        sendExitChatRoom(chatRoomId, userId);  // 유저가 퇴장했음을 알림
    }

    // 유저 내보내기(강퇴)
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

        sendExportChatRoom(chatRoomId, userIdToRemove, userId);
    }
}
