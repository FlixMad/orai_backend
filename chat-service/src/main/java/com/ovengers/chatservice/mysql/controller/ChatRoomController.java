package com.ovengers.chatservice.mysql.controller;

import com.ovengers.chatservice.client.UserResponseDto;
import com.ovengers.chatservice.common.auth.TokenUserInfo;
import com.ovengers.chatservice.mysql.dto.ChatRoomDto;
import com.ovengers.chatservice.mysql.dto.ChatRoomRequestDto;
import com.ovengers.chatservice.mysql.dto.CompositeChatRoomDto;
import com.ovengers.chatservice.mysql.service.ChatRoomService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "ChatController", description = "채팅방 관련 controller")
public class ChatRoomController {
    private final ChatRoomService chatRoomService;

    @GetMapping("/{userId}/profile")
    public ResponseEntity<UserResponseDto> getUserFromUserService(@PathVariable String userId) {
        UserResponseDto userResponse = chatRoomService.getUserInfo(userId);
        return ResponseEntity.ok(userResponse);
    }

    @PostMapping("/createChatRoom")
    public ResponseEntity<CompositeChatRoomDto> createChatRoom(@RequestBody ChatRoomRequestDto chatRoomRequestDto,
                                                               @AuthenticationPrincipal TokenUserInfo tokenUserInfo) {
        CompositeChatRoomDto createdChatRoom = chatRoomService.createChatRoom(
                chatRoomRequestDto.getImage(),
                chatRoomRequestDto.getName(),
                tokenUserInfo.getId(),
                chatRoomRequestDto.getUserIds() // 초대할 유저 ID 목록 전달
        );
        return ResponseEntity.ok(createdChatRoom);
    }

    @GetMapping("/chatRoomList")
    public ResponseEntity<List<ChatRoomDto>> ChatRoomList(@AuthenticationPrincipal TokenUserInfo tokenUserInfo) {
        List<ChatRoomDto> chatRoomList = chatRoomService.getChatRooms(tokenUserInfo.getId());
        return ResponseEntity.ok(chatRoomList);
    }

    @GetMapping("/{chatRoomId}/users")
    public ResponseEntity<List<UserResponseDto>> getSubscribedUsers(
            @PathVariable Long chatRoomId,
            @AuthenticationPrincipal TokenUserInfo tokenUserInfo) {
        List<UserResponseDto> subUsers = chatRoomService.getSubUsers(chatRoomId, tokenUserInfo.getId());
        return ResponseEntity.ok(subUsers);
    }

    @PutMapping("/{chatRoomId}/updateChatRoom")
    public ResponseEntity<ChatRoomDto> updateChatRoom(@PathVariable Long chatRoomId,
                                                      @RequestBody ChatRoomRequestDto chatRoomRequestDto,
                                                      @AuthenticationPrincipal TokenUserInfo tokenUserInfo) {
        ChatRoomDto updateChatRoom = chatRoomService.updateChatRoom(chatRoomId, chatRoomRequestDto.getImage(), chatRoomRequestDto.getName(), tokenUserInfo.getId());
        return ResponseEntity.ok(updateChatRoom);
    }

    @DeleteMapping("/{chatRoomId}/deleteChatRoom")
    public ResponseEntity<Void> deleteChatRoom(@PathVariable Long chatRoomId,
                                               @AuthenticationPrincipal TokenUserInfo tokenUserInfo) {
        chatRoomService.deleteChatRoom(chatRoomId, tokenUserInfo.getId());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{chatRoomId}/disconnect")
    public ResponseEntity<Void> disconnect(@PathVariable Long chatRoomId,
                                           @AuthenticationPrincipal TokenUserInfo tokenUserInfo) {
        chatRoomService.disconnectChatRoom(chatRoomId, tokenUserInfo.getId());
        return ResponseEntity.noContent().build();
    }
}