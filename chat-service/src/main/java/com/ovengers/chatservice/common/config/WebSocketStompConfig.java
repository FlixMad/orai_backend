package com.ovengers.chatservice.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker // WebSocket을 이용한 메시징 기능 사용
public class WebSocketStompConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/stomp") // STOMP 프로토콜을 사용하기 위한 엔드포인트 등록 "localhost:{port}/stomp"
                .setAllowedOriginPatterns("*") // 모든 도메인에서의 접근을 허용
                .withSockJS(); // WebSocket이 지원되지 않는 브라우저에서도 대체 옵션을 제공
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/sub"); // 메시지 수신
        registry.setApplicationDestinationPrefixes("/pub"); // 메시지 발신
    }
}