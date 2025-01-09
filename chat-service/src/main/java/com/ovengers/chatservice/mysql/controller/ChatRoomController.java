package com.ovengers.chatservice.mysql.controller;

import com.ovengers.chatservice.common.auth.TokenUserInfo;
import com.ovengers.chatservice.common.dto.CommonResDto;
import com.ovengers.chatservice.mysql.dto.ChatRoomDto;
import com.ovengers.chatservice.mysql.dto.ChatRoomRequestDto;
import com.ovengers.chatservice.mysql.service.ChatRoomService;
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

    // 채팅방 리스트 조회
    @GetMapping("/chatRoomList")
    public ResponseEntity<List<ChatRoomDto>> getChatRoomList(@AuthenticationPrincipal TokenUserInfo tokenUserInfo) {
        log.info("채팅방 리스트 조회 요청 - 사용자 ID: {}", tokenUserInfo.getId());
        List<ChatRoomDto> list = chatRoomService.getAllChatRooms(tokenUserInfo.getId());
        log.info("채팅방 리스트 조회 완료 - 결과 개수: {}", list.size());
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "채팅방 리스트 조회 완료", list);
        return new ResponseEntity(commonResDto, HttpStatus.OK);
    }

    // 새로운 채팅방 생성
    @PostMapping("/createChatRoom")
    public ResponseEntity<ChatRoomDto> createChatRoom(@AuthenticationPrincipal TokenUserInfo tokenUserInfo,
                                                      @RequestBody ChatRoomRequestDto requestDTO) {
        ChatRoomDto newChatRoom = chatRoomService.createChatRoom(requestDTO.getName(), tokenUserInfo);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "새로운 채팅방 생성 완료", newChatRoom);
        return new ResponseEntity(commonResDto, HttpStatus.OK);
    }

    // 채팅방 이름 수정
    @PutMapping("/{chatRoomId}/updateChatRoom")
    public ResponseEntity<ChatRoomDto> updateChatRoom(@AuthenticationPrincipal TokenUserInfo tokenUserInfo,
                                                      @PathVariable Long chatRoomId,
                                                      @RequestBody ChatRoomRequestDto requestDTO) {
        ChatRoomDto updatedChatRoom = chatRoomService.updateChatRoom(chatRoomId, requestDTO.getName(), tokenUserInfo);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "채팅방 이름 수정 완료", updatedChatRoom);
        return new ResponseEntity(commonResDto, HttpStatus.OK);
    }

    // 채팅방 삭제
    @DeleteMapping("/{chatRoomId}/deleteChatRoom")
    public ResponseEntity<Void> deleteChatRoom(@AuthenticationPrincipal TokenUserInfo tokenUserInfo,
                                               @PathVariable Long chatRoomId) {
        chatRoomService.deleteChatRoom(chatRoomId, tokenUserInfo);
        CommonResDto<Void> commonResDto = new CommonResDto<>(HttpStatus.OK, "채팅방 삭제 완료", null);
        return new ResponseEntity(commonResDto, HttpStatus.OK);
    }

}