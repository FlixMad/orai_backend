package com.ovengers.chatservice.repository;

import com.ovengers.chatservice.entity.Message;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public interface MessageRepository extends MongoRepository<Message, String> {
    List<Message> findAllByOrderByCreatedAtDesc(Long chatRoomId);

    Message findByChatRoomId(Long chatRoomId);
}
