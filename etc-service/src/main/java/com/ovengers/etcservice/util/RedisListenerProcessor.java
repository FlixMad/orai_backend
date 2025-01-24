package com.ovengers.etcservice.util;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class RedisListenerProcessor {

    private final RedisMessageListenerContainer container;

    @PostConstruct
    public void registerRedisListeners() {
        // Spring 컨텍스트에서 @RedisListener가 붙은 모든 메서드를 검색
        Map<String, Object> beans = ApplicationContextProvider.getApplicationContext().getBeansWithAnnotation(Component.class);
        beans.values().forEach(this::processBean);
    }

    private void processBean(Object bean) {
        Method[] methods = bean.getClass().getDeclaredMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(RedisListener.class)) {
                RedisListener annotation = method.getAnnotation(RedisListener.class);
                String topic = annotation.topic();

                // RedisMessageListenerContainer에 리스너 등록
                container.addMessageListener(
                        (message, pattern) -> invokeMethod(bean, method, message),
                        new PatternTopic(topic)
                );
            }
        }
    }

    private void invokeMethod(Object bean, Method method, Message message) {
        try {
            // 메시지 본문을 String으로 변환하여 메서드에 전달
            String messageBody = new String(message.getBody());
            method.invoke(bean, messageBody);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
