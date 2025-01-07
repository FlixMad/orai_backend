package com.ovengers.chatservice.mysql.repository;

import com.ovengers.chatservice.mysql.entity.UnreadMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UnreadMessageRepository extends JpaRepository<UnreadMessage, Long> {

    // 특정 채팅방과 사용자에 대한 읽지 않은 메시지 조회
    Optional<UnreadMessage> findByChatRoomIdAndUserId(Long chatRoomId, String userId);

    // 특정 채팅방의 모든 읽지 않은 메시지 수 조회
    List<UnreadMessage> findByChatRoomId(Long chatRoomId);

    // 읽지 않은 메시지 수 증가 (JPQL 사용)
    @Modifying
    @Query("UPDATE UnreadMessage u SET u.unreadCount = u.unreadCount + 1 " +
            "WHERE u.chatRoomId = :chatRoomId AND u.userId != :userId")
    void incrementUnreadCountForOtherUsers(@Param("chatRoomId") Long chatRoomId, @Param("userId") String userId);

    // 특정 사용자에 대한 읽지 않은 메시지 수 초기화
    @Modifying
    @Query("UPDATE UnreadMessage u SET u.unreadCount = 0 " +
            "WHERE u.chatRoomId = :chatRoomId AND u.userId = :userId")
    void resetUnreadCount(@Param("chatRoomId") Long chatRoomId, @Param("userId") String userId);

}
