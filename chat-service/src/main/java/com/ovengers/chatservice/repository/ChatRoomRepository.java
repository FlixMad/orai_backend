package com.ovengers.chatservice.repository;

import com.ovengers.chatservice.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    // 최신순으로 전체 채팅방 조회
    List<ChatRoom> findAllByOrderByCreatedAtDesc();
}
