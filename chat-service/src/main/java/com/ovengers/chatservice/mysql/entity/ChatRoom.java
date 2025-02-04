package com.ovengers.chatservice.mysql.entity;

import com.ovengers.chatservice.mysql.dto.ChatRoomDto;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@NoArgsConstructor
@ToString
@AllArgsConstructor
@Builder
@Entity
@Table(name = "tbl_chat_room")
public class ChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_room_id")
    private Long chatRoomId;

    @Setter
    @Column(name = "image")
    private String image;

    @Setter
    @Column(name = "name", length = 100)
    private String name;

    @Column(name = "creator_id", nullable = false)
    private String creatorId;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public ChatRoomDto toDto() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return ChatRoomDto.builder()
                .chatRoomId(chatRoomId)
                .image(image)
                .name(name)
                .creatorId(creatorId)
                .createdAt(createdAt !=null ? createdAt.format(formatter):"채팅방생성시간없음")
                .updatedAt(updatedAt !=null ? updatedAt.format(formatter):"채팅방수정시간없음")
                .build();
    }
}
