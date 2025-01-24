package com.ovengers.etcservice.service;

import com.ovengers.etcservice.dto.NotificationMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Value;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
    @Value("${spring.application.name}")
    private String instanceId;

    private final RedisTemplate<String, String> redisTemplate;
    private final SseConnectionService connectionService;

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