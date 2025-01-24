package com.ovengers.etcservice.controller;

import com.ovengers.etcservice.common.auth.TokenUserInfo;
import com.ovengers.etcservice.common.dto.CommonResDto;
import com.ovengers.etcservice.dto.NotificationResDto;
import com.ovengers.etcservice.entity.Notification;
import com.ovengers.etcservice.service.NotificationService;
import com.ovengers.etcservice.service.SseConnectionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
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

//    @GetMapping
//    public CommonResDto<?> getNotification() {
//        return null;
//    }
//
//    @GetMapping("/connect")
//    public SseEmitter connect(@AuthenticationPrincipal TokenUserInfo userInfo) {
//        SseEmitter emitter = new SseEmitter(30 * 60 * 1000L); // 30분 타임아웃
//        String userId = userInfo.getId();
//        clients.put(userId, emitter);
//
//        // 연결이 닫혔을 때 처리
//        emitter.onCompletion(() -> clients.remove(userId));
//        emitter.onTimeout(() -> clients.remove(userId));
//
//        // 연결 성공 메세지 전송
//        try {
//            emitter.send(SseEmitter.event()
//                    .name("connect")
//                    .data("connected!!!"));
//
//            // 30초마다 heartbeat 메시지를 전송하여 연결 유지
//            // 클라이언트에서 사용하는 EventSourcePolyfill이 45초 동안 활동이 없으면 지맘대로 연결 종료
//            Executors.newScheduledThreadPool(1).scheduleAtFixedRate(() -> {
//                try {
//                    emitter.send(SseEmitter.event()
//                            .name("heartbeat")
//                            .data("keep-alive")); // 클라이언트 단이 살아있는지 확인
//                } catch (IOException e) {
//                    log.warn("Failed to send heartbeat, removing emitter for email: {}", userId);
//                }
//            }, 30, 30, TimeUnit.SECONDS); // 30초마다 heartbeat 메시지 전송
//        } catch (IOException e) {
//            log.error("Failed to send connect message to admin: {}", userId);
//            log.error(e.getMessage());
//        }
//        return emitter;
//    }
//
//    @PostMapping("/schedules")
//    public CommonResDto<?> createSchedulesNotification(List<NotificationResDto> dtoList) {
//        dtoList.forEach(dto -> {
//            Notification newNotification = Notification.builder()
//                    .userId(dto.getUserId())
//                    .message(dto.getMessage())
//                    .build();
//            notificationService.createNotification(newNotification);
//        });
//        return null;
//    }


}
