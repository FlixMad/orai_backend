package com.ovengers.chatservice.controller;

import com.ovengers.chatservice.common.dto.CommonResDto;
import com.ovengers.chatservice.dto.MessageDto;
import com.ovengers.chatservice.dto.ChatRoomDto;
import com.ovengers.chatservice.dto.ChatRoomRequestDto;
import com.ovengers.chatservice.service.ChatRoomService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "ChatController", description = "채팅방 관련 controller")
public class ChatController {

    private final SimpMessageSendingOperations template;
    private final ChatRoomService chatRoomService;

    // 채팅방 리스트 조회
    @GetMapping("/chatRoomList")
    public ResponseEntity<List<ChatRoomDto>> getChatRoomList() {
        List<ChatRoomDto> list = chatRoomService.getAllChatRooms();
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "채팅방 리스트 조회 완료", list);
        return new ResponseEntity(commonResDto, HttpStatus.OK);
    }

        // 새로운 채팅방 생성
    @PostMapping("/createChatRoom")
    public ResponseEntity<ChatRoomDto> createChatRoom(@RequestBody ChatRoomRequestDto requestDTO) {
        ChatRoomDto newChatRoom = chatRoomService.createChatRoom(requestDTO.getName());
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "새로운 채팅방 생성 완료", newChatRoom);
        return new ResponseEntity(commonResDto, HttpStatus.OK);
    }

    // 채팅방 이름 수정
    @PutMapping("/{chatRoomId}/update")
    public ResponseEntity<ChatRoomDto> updateChatRoom(@PathVariable Long chatRoomId, @RequestBody ChatRoomRequestDto requestDTO) {
        ChatRoomDto updatedChatRoom = chatRoomService.updateChatRoom(chatRoomId, requestDTO.getName());
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "채팅방 이름 수정 완료", updatedChatRoom);
        return new ResponseEntity(commonResDto, HttpStatus.OK);
    }

    // 채팅방 삭제
    @DeleteMapping("/{chatRoomId}/delete")
    public ResponseEntity<Void> deleteChatRoom(@PathVariable Long chatRoomId) {
        chatRoomService.deleteChatRoom(chatRoomId);
        CommonResDto<Void> commonResDto = new CommonResDto<>(HttpStatus.OK, "채팅방 삭제 완료", null);
        return new ResponseEntity(commonResDto, HttpStatus.OK);
    }

/*    // 채팅 리스트 반환
    @GetMapping("/chat/{id}")
    public ResponseEntity<List<MessageDto>> getChatMessages(@PathVariable Long id){
        //임시로 리스트 형식으로 구현, 실제론 DB 접근 필요
        MessageDto test = new MessageDto(1L, "test", "test");
        return ResponseEntity.ok().body(List.of(test));
    }

    //메시지 송신 및 수신, /pub가 생략된 모습. 클라이언트 단에선 /pub/message로 요청
    @MessageMapping("/message")
    public ResponseEntity<Void> receiveMessage(@RequestBody MessageDto chat) {
        // 메시지를 해당 채팅방 구독자들에게 전송
        template.convertAndSend("/sub/chatroom/1", chat);
        return ResponseEntity.ok().build();
    }*/
}