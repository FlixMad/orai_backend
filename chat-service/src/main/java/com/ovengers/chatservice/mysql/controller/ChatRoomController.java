package com.ovengers.chatservice.mysql.controller;

import com.ovengers.chatservice.client.UserResponseDto;
import com.ovengers.chatservice.common.auth.TokenUserInfo;
import com.ovengers.chatservice.common.config.AwsS3Config;
import com.ovengers.chatservice.mysql.dto.ChatRoomDto;
import com.ovengers.chatservice.mysql.dto.CompositeChatRoomDto;
import com.ovengers.chatservice.mysql.service.ChatRoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "ChatController", description = "채팅방 관련 controller")
public class ChatRoomController {
    private final ChatRoomService chatRoomService;
    private final AwsS3Config s3Config;

    public String cleanInput(String input) {
        if (input == null) {
            return null;
        }
        // 문자열 양 끝의 쌍따옴표만 제거
        return input.startsWith("\"") && input.endsWith("\"")
                ? input.substring(1, input.length() - 1)
                : input;
    }

    @Operation(summary = "유저 프로필 조회", description = "유저Id")
    @GetMapping("/{userId}/profile")
    public ResponseEntity<UserResponseDto> getUserFromUserService(@PathVariable String userId) {
        UserResponseDto userResponse = chatRoomService.getUserInfo(userId);
        return ResponseEntity.ok(userResponse);
    }

    @Operation(summary = "유저 리스트 생성")
    @PostMapping("/userList")
    public ResponseEntity<List<UserResponseDto>> getAllUsers(@RequestBody List<String> userIds) {
        List<UserResponseDto> getAllUsers = chatRoomService.getAllUsers(userIds);
        return ResponseEntity.ok(getAllUsers);
    }

    @Operation(summary = "채팅방 생성", description = "이미지, 제목, 유저Ids")
    @PostMapping(value = "/createChatRoom", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CompositeChatRoomDto> createChatRoom(@RequestPart(value = "image") MultipartFile image,
                                                               @RequestParam String name,
                                                               @AuthenticationPrincipal TokenUserInfo tokenUserInfo,
                                                               @RequestParam List<String> userIds) throws IOException {
        // userIds에서 각 유저 ID의 앞뒤 따옴표 제거
        List<String> cleanedUserIds = userIds.stream()
                .map(this::cleanInput)
                .toList();

        log.info("\n\n\n" + cleanedUserIds + "\n\n\n");

        String uniqueFileName;
        if (image != null && !image.isEmpty()) {
            uniqueFileName = UUID.randomUUID() + "_" + image.getOriginalFilename();
            String imageUrl = s3Config.uploadToS3Bucket(image.getBytes(), uniqueFileName);

            CompositeChatRoomDto compositeChatRoomDto = chatRoomService.createChatRoom(
                    imageUrl,
                    name,
                    tokenUserInfo.getId(),
                    cleanedUserIds
            );

            return ResponseEntity.ok(compositeChatRoomDto);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @Operation(summary = "구독한 채팅방 목록")
    @GetMapping("/chatRoomList")
    public ResponseEntity<List<ChatRoomDto>> ChatRoomList(@AuthenticationPrincipal TokenUserInfo tokenUserInfo) {
        List<ChatRoomDto> chatRoomList = chatRoomService.getChatRooms(tokenUserInfo.getId());
        return ResponseEntity.ok(chatRoomList);
    }

    @Operation(summary = "채팅방을 구독한 유저 목록", description = "채팅방Id")
    @GetMapping("/{chatRoomId}/users")
    public ResponseEntity<List<UserResponseDto>> getSubscribedUsers(@PathVariable Long chatRoomId,
                                                                    @AuthenticationPrincipal TokenUserInfo tokenUserInfo) {
        List<UserResponseDto> subUsers = chatRoomService.getSubUsers(chatRoomId, tokenUserInfo.getId());
        return ResponseEntity.ok(subUsers);
    }

    @Operation(summary = "채팅방에 유저 초대", description = "채팅방Id, 유저Id")
    @PostMapping("/{chatRoomId}/invite")
    public ResponseEntity<Void> inviteUsers(@PathVariable Long chatRoomId,
                                            @AuthenticationPrincipal TokenUserInfo tokenUserInfo,
                                            @RequestParam List<String> inviteeIds) {
        // userIds에서 각 유저 ID의 앞뒤 따옴표 제거
        List<String> cleanedUserIds = inviteeIds.stream()
                .map(this::cleanInput)
                .toList();

        log.info("\n\n\n" + cleanedUserIds + "\n\n\n");

        chatRoomService.inviteUsers(chatRoomId, tokenUserInfo.getId(), cleanedUserIds);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "초대 수락", description = "채팅방Id - 수락을 하면 채팅방에 구독됨")
    @PostMapping("/{chatRoomId}/accept")
    public ResponseEntity<Void> acceptInvitation(@PathVariable Long chatRoomId,
                                                 @AuthenticationPrincipal TokenUserInfo tokenUserInfo) {

        chatRoomService.acceptInvitation(chatRoomId, tokenUserInfo.getId());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "초대 거절", description = "채팅방Id - 거절을 하면 초대 이력 삭제됨")
    @PostMapping("/{chatRoomId}/refusal")
    public ResponseEntity<Void> refusalInvitation(@PathVariable Long chatRoomId,
                                                  @AuthenticationPrincipal TokenUserInfo tokenUserInfo) {

        chatRoomService.refusalInvitation(chatRoomId, tokenUserInfo.getId());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "채팅방 수정", description = "채팅방Id, 이미지/제목 - 이미지나 제목 중 하나만 수정해도 됨")
    @PutMapping(value = "/{chatRoomId}/updateChatRoom", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ChatRoomDto> updateChatRoom(@PathVariable Long chatRoomId,
                                                      @RequestPart(value = "image", required = false) MultipartFile image,
                                                      @RequestParam String name,
                                                      @AuthenticationPrincipal TokenUserInfo tokenUserInfo) throws IOException {
        if (image != null && !image.isEmpty()) {
            String uniqueFileName = UUID.randomUUID() + "_" + image.getOriginalFilename();
            String imageUrl = s3Config.uploadToS3Bucket(image.getBytes(), uniqueFileName);

            ChatRoomDto chatRoomDto = chatRoomService.updateChatRoom(
                    chatRoomId,
                    imageUrl,
                    name,
                    tokenUserInfo.getId()
            );
            log.info("\n\n\n" + chatRoomDto + "\n\n\n");
            return ResponseEntity.ok(chatRoomDto);
        } else {
            ChatRoomDto chatRoomDto = chatRoomService.updateChatRoom(
                    chatRoomId,
                    "",
                    name,
                    tokenUserInfo.getId()
            );
            return ResponseEntity.ok(chatRoomDto);
        }
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
    public ResponseEntity<Void> removeUserFromChatRoom(@PathVariable Long chatRoomId,
                                                       @PathVariable String userId,
                                                       @AuthenticationPrincipal TokenUserInfo tokenUserInfo) {
        chatRoomService.removeUserFromChatRoom(chatRoomId, userId, tokenUserInfo.getId());
        return ResponseEntity.noContent().build();
    }
}