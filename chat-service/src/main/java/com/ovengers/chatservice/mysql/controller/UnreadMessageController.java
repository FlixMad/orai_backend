package com.ovengers.chatservice.mysql.controller;

import com.ovengers.chatservice.common.auth.TokenUserInfo;
import com.ovengers.chatservice.mysql.dto.UnreadMessageDto;
import com.ovengers.chatservice.mysql.service.UnreadMessageService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/unreadMessages")
@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "UnreadMessageController", description = "읽지 않은 메시지 관련 controller")
public class UnreadMessageController {

    private final UnreadMessageService unreadMessageService;

    // 읽지 않은 메시지 수 증가 (테스트용 또는 관리자용)
    @PostMapping("/{chatRoomId}/increment")
    public ResponseEntity<Void> incrementUnreadCount(@PathVariable Long chatRoomId,
                                                     @AuthenticationPrincipal TokenUserInfo tokenUserInfo) {
        String userId = tokenUserInfo.getId();
        unreadMessageService.incrementUnreadCount(chatRoomId, userId);
        return ResponseEntity.ok().build();
    }

    // 특정 사용자의 읽지 않은 메시지 수 조회
    @GetMapping("/{chatRoomId}/user")
    public ResponseEntity<UnreadMessageDto> getUnreadCount(@PathVariable Long chatRoomId,
                                                           @AuthenticationPrincipal TokenUserInfo tokenUserInfo) {
        UnreadMessageDto unreadCount = unreadMessageService.getUnreadCount(chatRoomId, tokenUserInfo.getId());
        return ResponseEntity.ok(unreadCount);
    }

    // 특정 채팅방의 모든 읽지 않은 메시지 수 조회
    @GetMapping("/{chatRoomId}/all")
    public ResponseEntity<List<UnreadMessageDto>> getUnreadCountsByChatRoom(@PathVariable Long chatRoomId) {
        List<UnreadMessageDto> unreadCounts = unreadMessageService.getUnreadCountsByChatRoom(chatRoomId);
        return ResponseEntity.ok(unreadCounts);
    }

    // 읽지 않은 메시지 수 초기화
    @PostMapping("/{chatRoomId}/reset")
    public ResponseEntity<Void> resetUnreadCount(@PathVariable Long chatRoomId,
                                                 @AuthenticationPrincipal TokenUserInfo tokenUserInfo) {
        unreadMessageService.resetUnreadCount(chatRoomId, tokenUserInfo.getId());
        return ResponseEntity.ok().build();
    }
}
