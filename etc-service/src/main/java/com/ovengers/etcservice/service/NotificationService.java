package com.ovengers.etcservice.service;

import com.ovengers.etcservice.entity.Notification;
import com.ovengers.etcservice.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final RedisMessageListenerContainer redisMessageListenerContainer;
    private static final String REDIS_CHANNEL = "notifications";

    @Qualifier("sse-template") // sse 전용 레디스 빈을 주입
    private final RedisTemplate<String, Object> sseRedisTemplate;

    public void createNotification(Notification newNotification) {
        notificationRepository.save(newNotification);
    }
    //레디스에 알림 생성되었음을 pub
    public void sendMessage(String userId) {
        log.info("Notification sent to Redis: {}", userId);
        sseRedisTemplate.convertAndSend(REDIS_CHANNEL, userId);
    }

    //레디스에서 sub 메세지 왔을 때 뭐 동작할지 정함 (메세지 리스너 등록)
    private void subscribeChannel(SseEmitter emitter) {
        // 메시지가 수신된다면 어떤 객체의 어떤 메서드로 처리할 것인지를 객체 생성 때 알려줘야 한다.
        MessageListenerAdapter adapter
                //메서드 매개 변수 설정 어캐함
                = new MessageListenerAdapter(this,"sendNotification");
        redisMessageListenerContainer.addMessageListener(adapter, new PatternTopic(REDIS_CHANNEL));
    }
    // 클라이언트 단에 알림 전송(UI 업데이트)
    public void sendNotification(String userId, String message, ConcurrentHashMap<String, SseEmitter> clients) {
        if (clients.containsKey(userId)){
            SseEmitter emitter = clients.get(userId);
            if (emitter != null) {
                try {
                    emitter.send(SseEmitter.event()
                            .name("notification")
                            .data(message));
                } catch (IOException e) {
                    clients.remove(userId); // 실패 시 클라이언트 제거
                }
            }
        }
    }


}
