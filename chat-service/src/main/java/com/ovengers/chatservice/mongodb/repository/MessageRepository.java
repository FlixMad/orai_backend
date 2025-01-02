package com.ovengers.chatservice.mongodb.repository;

import com.ovengers.chatservice.mongodb.document.Message;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface MessageRepository extends ReactiveMongoRepository<Message, ObjectId> {
    Flux<Message> findAllByChatRoomId(Long chatRoomId);
}
