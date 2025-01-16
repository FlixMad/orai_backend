package com.ovengers.chatservice.mongodb.repository;

import com.ovengers.chatservice.mongodb.document.Message;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface MessageRepository extends ReactiveMongoRepository<Message, String> {
    Flux<Message> findAllByChatRoomId(Long chatRoomId);

    Mono<Message> existsByMessageId(String messageId);

    Mono<Message> findByMessageId(String messageId);
}
