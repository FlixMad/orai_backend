package com.ovengers.etcservice.controller;

import com.ovengers.etcservice.common.auth.TokenUserInfo;
import com.ovengers.etcservice.common.dto.CommonResDto;
import com.ovengers.etcservice.dto.NotificationResDto;
import com.ovengers.etcservice.entity.Notification;
import com.ovengers.etcservice.service.NotificationService;
import com.ovengers.etcservice.service.SseConnectionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class NotificationController {
    private final NotificationService notificationService;
    private final SseConnectionService connectionService;
    private final Map<String, SseEmitter> clients = new ConcurrentHashMap<>();

    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@AuthenticationPrincipal String userId) {
        return connectionService.connect(userId);
    }

    @GetMapping
    public ResponseEntity<?> getNotifications(@AuthenticationPrincipal TokenUserInfo tokenUserInfo) {
        List<NotificationResDto> notification = notificationService.getNotification(tokenUserInfo.getId());
        CommonResDto<?> commonResDto = new CommonResDto<>(HttpStatus.OK,"알림 조회 완료", notification);
        return ResponseEntity.ok(commonResDto);
    }
    @GetMapping("/count")
    public ResponseEntity<?> getNotificationCount(@AuthenticationPrincipal TokenUserInfo tokenUserInfo) {

    }


}
