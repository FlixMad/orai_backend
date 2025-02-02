package com.ovengers.etcservice.service;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Service
public class SseConnectionService {

    //유레카꺼 쓰는거 개찝찝한디.
//    @Value("${eureka.client.instance.instance-id}")
    @Value("${HOSTNAME:localhost}") // 쿠버네티스 환경에서 HOSTNAME 사용, 없으면 'localhost'로 대체
    private String hostname;

    @Getter
    private String instanceId;

    @PostConstruct
    public void init() {
        this.instanceId = hostname + "-" + UUID.randomUUID();
        System.out.println("Generated Instance ID: " + instanceId);
    }

    @Qualifier("sse-template")
    private final RedisTemplate<String, Object> redisTemplate;
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    public SseEmitter connect(String userId) {
        SseEmitter emitter = new SseEmitter(60 * 60 * 1000L); // 1시간

        // Redis Hash에 연결 정보 저장
        String connectionInfo = String.format("%s:%s", instanceId, emitter.hashCode());
        redisTemplate.opsForHash().put("user:connections", userId, connectionInfo);

        // 로컬 캐시에 저장
        emitters.put(userId, emitter);

        // 연결 종료 시 cleanup
//        emitter.onCompletion(() -> removeEmitter(userId));
        emitter.onTimeout(() -> removeEmitter(userId));

        try {
            // 연결 성공 메시지 전송
            emitter.send(SseEmitter.event()
                    .name("connect")
                    .data("Connected to notification service"));
            //            // 30초마다 heartbeat 메시지를 전송하여 연결 유지
            // 클라이언트에서 사용하는 EventSourcePolyfill이 45초 동안 활동이 없으면 지맘대로 연결 종료
            Executors.newScheduledThreadPool(1).scheduleAtFixedRate(() -> {
                try {
                    emitter.send(SseEmitter.event()
                            .name("heartbeat")
                            .data("keep-alive")); // 클라이언트 단이 살아있는지 확인
                } catch (IOException e) {
                    log.warn("Failed to send heartbeat, removing emitter for userId: {}", userId);
                }
            }, 30, 30, TimeUnit.SECONDS); // 30초마다 heartbeat 메시지 전송
        } catch (IOException e) {
            log.error("Failed to send connection message to user {}", userId);
//            removeEmitter(userId);
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
