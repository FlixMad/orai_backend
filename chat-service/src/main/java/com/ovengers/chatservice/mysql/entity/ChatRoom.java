package com.ovengers.chatservice.mysql.entity;

import com.ovengers.chatservice.mysql.dto.ChatRoomDto;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    @Column(name = "image")
    private String image;

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "creator_id", nullable = false)
    private String creatorId;

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserChatRoom> userChatRooms = new ArrayList<>();

    public ChatRoomDto toDto() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return ChatRoomDto.builder()
                .chatRoomId(chatRoomId)
                .image(image)
                .name(name)
                .createdAt(createdAt !=null ? createdAt.format(formatter):"채팅방생성시간없음")
                .updatedAt(updatedAt !=null ? updatedAt.format(formatter):"채팅방수정시간없음")
                .creatorId(creatorId)
                .userIds(userChatRooms.stream()
                        .map(UserChatRoom::getUserId)
                        .collect(Collectors.toList()))
                .build();
    }

    public void setName(String cleanedName) {
        this.name = cleanedName;
    }

    public void setImage(String newImage) {
        this.image = newImage;
    }
}
