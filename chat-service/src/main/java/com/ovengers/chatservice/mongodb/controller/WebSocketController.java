package com.ovengers.chatservice.mongodb.controller;

import com.ovengers.chatservice.mongodb.service.MessageService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "ReactiveMongoDBMessageController", description = "리액티브 MongoDB 메시지 관련 controller")
public class WebSocketController {
    private final MessageService messageService;
}
