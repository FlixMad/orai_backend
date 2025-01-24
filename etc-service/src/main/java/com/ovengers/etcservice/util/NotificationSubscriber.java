package com.ovengers.etcservice.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ovengers.etcservice.dto.NotificationEvent;
import com.ovengers.etcservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationSubscriber implements MessageListener{
    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;


    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String messageBody = new String(message.getBody());
            NotificationEvent event = objectMapper.readValue(messageBody, NotificationEvent.class);
            event.getUserIds().forEach(userId ->
                    notificationService.handleNotification(userId, event.getMessage()));
        } catch (JsonProcessingException e) {
            log.error("Failed to process notification message", e);
        }
    }
}
