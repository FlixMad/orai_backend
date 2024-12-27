package com.ovengers.chatservice.repository;

import com.ovengers.chatservice.dto.ChatRoomDto;
import com.ovengers.chatservice.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    // 최신순으로 전체 채팅방 조회
    List<ChatRoom> findAllByOrderByCreatedAtDesc();

    // 이름으로 검색
    List<ChatRoom> findByName(String name);

    // 이름으로 검색 (부분 일치)
    List<ChatRoom> findByNameContaining(String keyword);

    // 이름으로 삭제
    void deleteByName(String name);
}
