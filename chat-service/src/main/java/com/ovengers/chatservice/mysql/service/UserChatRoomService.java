//package com.ovengers.chatservice.mysql.service;
//
//import com.ovengers.chatservice.mysql.dto.UserChatRoomDto;
//import com.ovengers.chatservice.mysql.entity.UserChatRoom;
//import com.ovengers.chatservice.mysql.repository.UserChatRoomRepository;
//import jakarta.transaction.Transactional;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@Service
//@RequiredArgsConstructor
//public class UserChatRoomService {
//    private final UserChatRoomRepository userChatRoomRepository;
//
//    // 채팅방을 구독하거나 생성할 때 사용자를 저장
//    public void subscribeToChatRoom(UserChatRoomDto userChatRoomDto) {
//        UserChatRoom userChatRoom = UserChatRoom.builder()
//                .chatRoomId(userChatRoomDto.getChatRoomId())
//                .userId(userChatRoomDto.getUserId())
//                .build();
//        UserChatRoom savedUserChatRoom = userChatRoomRepository.save(userChatRoom);
//        savedUserChatRoom.toDto();
//    }
//
//    // 특정 사용자의 채팅방 구독 삭제
//    @Transactional
//    public void removeSubscriberFromChatRoom(Long chatRoomId, String userId) {
//        if (!userChatRoomRepository.existsByChatRoomIdAndUserId(chatRoomId, userId)) {
//            throw new IllegalArgumentException("해당 사용자는 채팅방에 구독되어 있지 않습니다.");
//        }
//        userChatRoomRepository.deleteByChatRoomIdAndUserId(chatRoomId, userId);
//    }
//
//    // 특정 채팅방의 모든 사용자 연결 삭제
//    @Transactional
//    public void removeAllSubscribersFromChatRoom(Long chatRoomId) {
//        List<UserChatRoom> subscribers = userChatRoomRepository.findAllByChatRoomId(chatRoomId);
//        if (subscribers.isEmpty()) {
//            throw new IllegalArgumentException("해당 채팅방에 구독된 사용자가 없습니다.");
//        }
//        userChatRoomRepository.deleteByChatRoomId(chatRoomId);
//    }
//
//}
