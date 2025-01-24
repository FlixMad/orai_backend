package com.ovengers.etcservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RequiredArgsConstructor
@Service
public class SseConnectionService {

    @Value("${spring.application.name}")
    private String instanceId;

    private final RedisTemplate<String, String> redisTemplate;
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    public SseEmitter connect(String userId) {
        SseEmitter emitter = new SseEmitter(60 * 60 * 1000L); // 1시간

        // Redis Hash에 연결 정보 저장
        String connectionInfo = String.format("%s:%s", instanceId, emitter.hashCode());
        redisTemplate.opsForHash().put("user:connections", userId, connectionInfo);

        // 로컬 캐시에 저장
        emitters.put(userId, emitter);

        // 연결 종료 시 cleanup
        emitter.onCompletion(() -> removeEmitter(userId));
        emitter.onTimeout(() -> removeEmitter(userId));

        try {
            // 연결 성공 메시지 전송
            emitter.send(SseEmitter.event()
                    .name("connect")
                    .data("Connected to notification service"));
        } catch (IOException e) {
            log.error("Failed to send connection message to user {}", userId);
            removeEmitter(userId);
        }

        return emitter;
    }

    public void removeEmitter(String userId) {
        emitters.remove(userId);
        redisTemplate.opsForHash().delete("user:connections", userId);
        log.info("Removed emitter for user {}", userId);
    }

    public SseEmitter getEmitter(String userId) {
        return emitters.get(userId);
    }
}
