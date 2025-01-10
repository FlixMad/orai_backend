package com.ovengers.chatservice.mysql.controller;

import com.ovengers.chatservice.common.auth.TokenUserInfo;
import com.ovengers.chatservice.common.dto.CommonResDto;
import com.ovengers.chatservice.mysql.dto.ChatRoomDto;
import com.ovengers.chatservice.mysql.dto.UserChatRoomDto;
import com.ovengers.chatservice.mysql.service.ChatRoomService;
import com.ovengers.chatservice.mysql.service.UserChatRoomService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
    private final UserChatRoomService userChatRoomService;

    // 채팅방 리스트 조회
    @GetMapping("/chatRoomList")
    public ResponseEntity<List<ChatRoomDto>> getChatRoomList(@AuthenticationPrincipal TokenUserInfo tokenUserInfo) {
        List<ChatRoomDto> list = chatRoomService.getAllChatRooms(tokenUserInfo.getId());
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "채팅방 리스트 조회 완료", list);
        return new ResponseEntity(commonResDto, HttpStatus.OK);
    }

    // 새로운 채팅방 생성
    @PostMapping("/createChatRoom")
    public ResponseEntity<ChatRoomDto> createChatRoom(@AuthenticationPrincipal TokenUserInfo tokenUserInfo,
                                                      @RequestBody String name) {
        ChatRoomDto newChatRoom = chatRoomService.createChatRoom(name, tokenUserInfo);

        // 채팅방 생성자 정보를 저장
        userChatRoomService.subscribeToChatRoom(
                UserChatRoomDto.builder()
                        .chatRoomId(newChatRoom.getChatRoomId())
                        .userId(tokenUserInfo.getId())
                        .build()
        );

        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "새로운 채팅방 생성 완료", newChatRoom);
        return new ResponseEntity(commonResDto, HttpStatus.OK);
    }

    // 채팅방 이름 수정
    @PutMapping("/{chatRoomId}/updateChatRoom")
    public ResponseEntity<ChatRoomDto> updateChatRoom(@AuthenticationPrincipal TokenUserInfo tokenUserInfo,
                                                      @PathVariable Long chatRoomId,
                                                      @RequestBody String newName) {
        ChatRoomDto updatedChatRoom = chatRoomService.updateChatRoom(chatRoomId, newName, tokenUserInfo);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "채팅방 이름 수정 완료", updatedChatRoom);
        return new ResponseEntity(commonResDto, HttpStatus.OK);
    }

    // 채팅방 삭제
    @DeleteMapping("/{chatRoomId}/deleteChatRoom")
    public ResponseEntity<Void> deleteChatRoom(@AuthenticationPrincipal TokenUserInfo tokenUserInfo,
                                               @PathVariable Long chatRoomId) {
        // 채팅방 삭제 (채팅방 생성자 권한 확인 포함)
        chatRoomService.deleteChatRoom(chatRoomId, tokenUserInfo);

        // 해당 채팅방과 연결된 모든 사용자와의 연결 삭제
        userChatRoomService.removeAllSubscribersFromChatRoom(chatRoomId);

        CommonResDto<Void> commonResDto = new CommonResDto<>(HttpStatus.OK, "채팅방 삭제 완료", null);
        return new ResponseEntity(commonResDto, HttpStatus.OK);
    }

}