package com.ovengers.chatservice.mongodb.repository;

import com.ovengers.chatservice.mongodb.entity.Message;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MessageRepository extends MongoRepository<Message, ObjectId> {
    List<Message> findByChatRoomId(Long chatRoomId);
}
