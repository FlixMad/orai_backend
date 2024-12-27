package com.ovengers.chatservice.service;

import com.ovengers.chatservice.dto.ChatRoomDto;
import com.ovengers.chatservice.entity.ChatRoom;
import com.ovengers.chatservice.repository.ChatRoomRepository;
import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Getter
@Transactional
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;

    public List<ChatRoomDto> chatRoomDtoList() {
        List<ChatRoom> chatRoomList = chatRoomRepository.findAllByOrderByCreatedAtDesc();
        return chatRoomList.stream()
                .map(list -> {
                    ChatRoomDto chatRoomDto = new ChatRoomDto();
                    chatRoomDto.setName(list.getName());
                    chatRoomDto.setCreatedAt(list.getCreatedAt());
                    log.info("chatRoomDto.getName: {}", chatRoomDto.getName());
                    return chatRoomDto;
                })
                .collect(Collectors.toList());
    }

}
