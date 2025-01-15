package com.ovengers.chatservice.mysql.repository;

import com.ovengers.chatservice.mysql.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    List<ChatRoom> findAllById(Iterable<Long> ids); // 특정 ID 목록에 해당하는 채팅방 조회

}
