package com.ovengers.chatservice.mongodb.controller;

import com.ovengers.chatservice.mongodb.dto.MessageDto;
import com.ovengers.chatservice.mongodb.service.MessageService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "MessageController", description = "메시지 관련 controller")
public class MessageController {
    private final MessageService messageService;

    @PostMapping("/send")
    public MessageDto sendMessage(@RequestBody MessageDto messageDto) {
        return messageService.saveChat(messageDto);
    }

    @GetMapping("/{chatRoomId}/chatRoom")
    public List<MessageDto> getChatsByRoomId(@PathVariable String chatRoomId) {
        return messageService.getMessageByChatRoomId(Long.valueOf(chatRoomId));
    }
}
