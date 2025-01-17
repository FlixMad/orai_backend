package com.ovengers.chatservice.mysql.controller;

import com.ovengers.chatservice.client.UserResponseDto;
import com.ovengers.chatservice.common.auth.TokenUserInfo;
import com.ovengers.chatservice.mysql.dto.ChatRoomDto;
import com.ovengers.chatservice.mysql.dto.ChatRoomRequestDto;
import com.ovengers.chatservice.mysql.dto.ChatRoomUpdateDto;
import com.ovengers.chatservice.mysql.dto.CompositeChatRoomDto;
import com.ovengers.chatservice.mysql.service.ChatRoomService;
import io.swagger.v3.oas.annotations.Operation;
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

    @Operation(summary = "유저 프로필 조회", description = "유저Id")
    @GetMapping("/{userId}/profile")
    public ResponseEntity<UserResponseDto> getUserFromUserService(@PathVariable String userId) {
        UserResponseDto userResponse = chatRoomService.getUserInfo(userId);
        return ResponseEntity.ok(userResponse);
    }

    @Operation(summary = "채팅방 생성", description = "이미지, 제목, 유저Ids")
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

    @Operation(summary = "구독한 채팅방 목록")
    @GetMapping("/chatRoomList")
    public ResponseEntity<List<ChatRoomDto>> ChatRoomList(@AuthenticationPrincipal TokenUserInfo tokenUserInfo) {
        List<ChatRoomDto> chatRoomList = chatRoomService.getChatRooms(tokenUserInfo.getId());
        return ResponseEntity.ok(chatRoomList);
    }

    @Operation(summary = "채팅방을 구독한 유저 목록", description = "채팅방Id")
    @GetMapping("/{chatRoomId}/users")
    public ResponseEntity<List<UserResponseDto>> getSubscribedUsers(
            @PathVariable Long chatRoomId,
            @AuthenticationPrincipal TokenUserInfo tokenUserInfo) {
        List<UserResponseDto> subUsers = chatRoomService.getSubUsers(chatRoomId, tokenUserInfo.getId());
        return ResponseEntity.ok(subUsers);
    }

    @Operation(summary = "채팅방에 유저 초대", description = "채팅방Id, 유저Id")
    @PostMapping("/{chatRoomId}/invite")
    public ResponseEntity<Void> inviteUsers(
            @PathVariable Long chatRoomId,
            @AuthenticationPrincipal TokenUserInfo tokenUserInfo,
            @RequestBody List<String> inviteeIds) {

        chatRoomService.inviteUsers(chatRoomId, tokenUserInfo.getId(), inviteeIds);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "초대 수락", description = "채팅방Id - 수락을 하면 채팅방에 구독됨")
    @PostMapping("/{chatRoomId}/accept")
    public ResponseEntity<Void> acceptInvitation(
            @PathVariable Long chatRoomId,
            @AuthenticationPrincipal TokenUserInfo tokenUserInfo) {

        chatRoomService.acceptInvitation(chatRoomId, tokenUserInfo.getId());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "초대 거절", description = "채팅방Id - 거절을 하면 초대 이력 삭제됨")
    @PostMapping("/{chatRoomId}/refusal")
    public ResponseEntity<Void> refusalInvitation(
            @PathVariable Long chatRoomId,
            @AuthenticationPrincipal TokenUserInfo tokenUserInfo) {

        chatRoomService.refusalInvitation(chatRoomId, tokenUserInfo.getId());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "채팅방 수정", description = "채팅방Id, 이미지/제목 - 이미지나 제목 중 하나만 수정해도 됨")
    @PutMapping("/{chatRoomId}/updateChatRoom")
    public ResponseEntity<ChatRoomDto> updateChatRoom(@PathVariable Long chatRoomId,
                                                      @RequestBody ChatRoomUpdateDto chatRoomUpdateDto,
                                                      @AuthenticationPrincipal TokenUserInfo tokenUserInfo) {
        ChatRoomDto updateChatRoom = chatRoomService.updateChatRoom(chatRoomId, chatRoomUpdateDto.getImage(), chatRoomUpdateDto.getName(), tokenUserInfo.getId());
        return ResponseEntity.ok(updateChatRoom);
    }

    @Operation(summary = "채팅방 삭제", description = "채팅방Id - chatRoom, userChatRoom, invitation에서 삭제됨")
    @DeleteMapping("/{chatRoomId}/deleteChatRoom")
    public ResponseEntity<Void> deleteChatRoom(@PathVariable Long chatRoomId,
                                               @AuthenticationPrincipal TokenUserInfo tokenUserInfo) {
        chatRoomService.deleteChatRoom(chatRoomId, tokenUserInfo.getId());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "채팅방 나가기", description = "채팅방Id - userChatRoom, invitation에서 삭제됨")
    @DeleteMapping("/{chatRoomId}/disconnect")
    public ResponseEntity<Void> disconnect(@PathVariable Long chatRoomId,
                                           @AuthenticationPrincipal TokenUserInfo tokenUserInfo) {
        chatRoomService.disconnectChatRoom(chatRoomId, tokenUserInfo.getId());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "채팅방 내보내기", description = "채팅방Id, 유저Id - userChatRoom, invitation에서 삭제됨")
    @DeleteMapping("/{chatRoomId}/{userId}/deleteUser")
    public ResponseEntity<Void> removeUserFromChatRoom(
            @PathVariable Long chatRoomId,
            @PathVariable String userId,
            @AuthenticationPrincipal TokenUserInfo tokenUserInfo) {
        chatRoomService.removeUserFromChatRoom(chatRoomId, userId, tokenUserInfo.getId());
        return ResponseEntity.noContent().build();
    }
}