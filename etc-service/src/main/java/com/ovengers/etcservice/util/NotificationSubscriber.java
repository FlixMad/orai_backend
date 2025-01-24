package com.ovengers.etcservice.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ovengers.etcservice.dto.NotificationEvent;
import com.ovengers.etcservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationSubscriber {
    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;

    @RedisListener(topic = "notifications")
    public void handleMessage(String message) {
        try {
            NotificationEvent event = objectMapper.readValue(message, NotificationEvent.class);
            event.getUserIds().forEach(userId ->
                    notificationService.handleNotification(userId, event.getMessage()));
        } catch (JsonProcessingException e) {
            log.error("Failed to process notification message", e);
        }
    }
}
