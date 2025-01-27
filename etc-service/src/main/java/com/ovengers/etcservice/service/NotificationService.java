package com.ovengers.etcservice.service;

import com.ovengers.etcservice.dto.NotificationMessage;
import com.ovengers.etcservice.dto.NotificationResDto;
import com.ovengers.etcservice.entity.Notification;
import com.ovengers.etcservice.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
    @Value("${spring.application.name}")
    private String instanceId;

    @Qualifier("sse-template")
    private final RedisTemplate<String, Object> redisTemplate;
    private final SseConnectionService connectionService;

    private final NotificationRepository notificationRepository;

    //알림 내용 조회 화면
    public List<NotificationResDto> getNotification(String userId) {
        List<Notification> allByUserId = notificationRepository.findAllByUserId(userId);
        List<NotificationResDto> list = allByUserId.stream().map(notification -> notification.toDto(notification)).toList();
        updateNotificationIsRead(allByUserId);
        return list;
    }
    //알림 읽음 내역 변경
    private void updateNotificationIsRead(List<Notification> notifications) {
        notifications.forEach(notification -> {
            notification.setRead(true);
        });
        notificationRepository.saveAll(notifications);
    }

    //sse 연결된 사용자가 알림 이벤트 목록에 있으면 SSE 알림 보냄
    public void handleNotification(String userId, NotificationMessage message) {
        String connectionInfo = (String) redisTemplate.opsForHash().get("user:connections", userId);

        if (connectionInfo != null && connectionInfo.startsWith(instanceId)) {
            SseEmitter emitter = connectionService.getEmitter(userId);
            if (emitter != null) {
                try {
                    emitter.send(SseEmitter.event()
                            .name("notification")
                            .data(message));
                    log.info("Notification sent to user {}", userId);
                } catch (IOException e) {
                    log.error("Failed to send notification to user {}", userId);
                    connectionService.removeEmitter(userId);
                }
            }
        }
    }
}