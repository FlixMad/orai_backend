package com.ovengers.chatservice.mysql.repository;

import com.ovengers.chatservice.mysql.entity.ChatRoomRead;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomReadRepository extends JpaRepository<ChatRoomRead, Long> {
    Optional<ChatRoomRead> findByChatRoomIdAndUserId(Long chatRoomId, String userId);
    List<ChatRoomRead> findAllByUserId(String userId);
    void deleteByChatRoomId(Long chatRoomId);
}