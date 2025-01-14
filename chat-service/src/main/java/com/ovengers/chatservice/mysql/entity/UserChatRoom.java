package com.ovengers.chatservice.mysql.entity;

import com.ovengers.chatservice.mysql.dto.UserChatRoomDto;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@NoArgsConstructor
@ToString
@AllArgsConstructor
@Builder
@Entity
@Table(name = "tbl_user_chat_room", uniqueConstraints = @UniqueConstraint(columnNames = {"chat_room_id", "user_id"}))
public class UserChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    @Column(name = "sub_at")
    private LocalDateTime subAt;

    @ManyToOne
    @JoinColumn(name = "chat_room_id", referencedColumnName = "chat_room_id")
    private ChatRoom chatRoom;

    @Column(name = "user_id", nullable = false)
    private String userId;

    public UserChatRoomDto toDto() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return UserChatRoomDto.builder()
                .id(id)
                .subAt(subAt!= null ? subAt.format(formatter) : "채팅방구독시간없음")
                .userId(userId)
                .chatRoomId(chatRoom.getChatRoomId())
                .build();
    }
}

