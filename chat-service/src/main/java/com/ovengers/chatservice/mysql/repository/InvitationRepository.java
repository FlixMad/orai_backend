package com.ovengers.chatservice.mysql.repository;

import com.ovengers.chatservice.mysql.entity.Invitation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InvitationRepository extends JpaRepository<Invitation, Long> {
    Optional<Invitation> findByChatRoomIdAndUserId(Long chatRoomId, String userId);
    boolean existsByChatRoomIdAndUserIdAndAcceptedFalse(Long chatRoomId, String userId);
    void deleteByChatRoomId(Long chatRoomId);
    void deleteByChatRoomIdAndUserId(Long chatRoomId, String userId);
}
