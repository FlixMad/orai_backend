package com.ovengers.chatservice.mongodb.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /*
        WebSocket과 STOMP 메시징을 위한 설정을 추가.
        이 설정은 클라이언트가 서버에 연결할 수 있는 엔드포인트와 메시지 브로커를 설정.
     */

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // stomp 접속 주소 url = ws://localhost:8181/ws
        // setAllowedOrigins("*")는 모든 ip에서 접속 가능하도록 해줌
        registry.addEndpoint("/connection") // 연결될 엔드포인트
                .setAllowedOriginPatterns("*")
                .withSockJS();
        registry.addEndpoint("/connection")
                .setAllowedOriginPatterns("*");
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 메시지를 구독(수신)하는 요청 엔드포인트
        registry.enableSimpleBroker("/sub");

        // 메시지를 발행(송신)하는 엔드포인트
        registry.setApplicationDestinationPrefixes("/pub");
    }
}