package com.ovengers.chatservice.mysql.repository;

import com.ovengers.chatservice.mysql.entity.UserChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserChatRoomRepository extends JpaRepository<UserChatRoom, Long> {

    List<UserChatRoom> findAllByUserId(String userId); // 특정 사용자가 구독 중인 항목 조회

    void deleteByChatRoomId(Long chatRoomId); // 특정 채팅방의 모든 구독 삭제

    List<UserChatRoom> findAllByChatRoomId(Long chatRoomId); // 특정 채팅방을 구독 중인 사용자 목록 조회

    boolean existsByChatRoomIdAndUserId(Long chatRoomId, String userId); // 특정 채팅방과 사용자의 구독 여부 확인

    void deleteByChatRoomIdAndUserId(Long chatRoomId, String userId); // 특정 채팅방과 사용자의 구독 삭제
}
