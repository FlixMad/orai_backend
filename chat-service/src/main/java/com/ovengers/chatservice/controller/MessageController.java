package com.ovengers.chatservice.controller;

import com.ovengers.chatservice.common.dto.CommonResDto;
import com.ovengers.chatservice.dto.MessageDto;
import com.ovengers.chatservice.dto.MessageRequestDto;
import com.ovengers.chatservice.service.MessageService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "MessageController", description = "메시지 관련 controller")
public class MessageController {
    private final MessageService messageService;

    // 채팅방마다의 메시지 전체 조회
    @GetMapping("/{chatRoomId}/messageList")
    public ResponseEntity<List<MessageDto>> getMessageList(@PathVariable Long chatRoomId) {
        List<MessageDto> list = messageService.getAllMessage(chatRoomId);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "채팅방마다의 메시지 전체 조회 완료", list);
        return new ResponseEntity(commonResDto, HttpStatus.OK);
    }

    // 메시지 내용 작성
    @PostMapping("/{chatRoomId}/createMessage")
    public ResponseEntity<MessageDto> createMessage(@PathVariable Long chatRoomId, @RequestBody MessageRequestDto requestDTO) {
        MessageDto newMessage = messageService.createMessage(chatRoomId, requestDTO.getContent());
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "메시지 내용 작성 완료", newMessage);
        return new ResponseEntity(commonResDto, HttpStatus.OK);
    }

    // 메시지 내용 수정
    @PutMapping("/{messageId}/update")
    public ResponseEntity<MessageDto> updateMessage(@PathVariable Long messageId, @RequestBody MessageRequestDto requestDTO) {
        MessageDto updatedMessage = messageService.updateMessage(messageId, requestDTO.getContent());
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "메시지 내용 수정 완료", updatedMessage);
        return new ResponseEntity(commonResDto, HttpStatus.OK);
    }

    // 메시지 내용 삭제
    @DeleteMapping("/{messageId}/delete")
    public ResponseEntity<Void> deleteMessage(@PathVariable Long messageId) {
        messageService.deleteMessage(messageId);
        CommonResDto<Void> commonResDto = new CommonResDto<>(HttpStatus.OK, "메시지 내용 삭제 완료", null);
        return new ResponseEntity(commonResDto, HttpStatus.OK);
    }

}
