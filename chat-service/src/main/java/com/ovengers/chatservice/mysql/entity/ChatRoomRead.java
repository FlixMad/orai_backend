package com.ovengers.chatservice.mysql.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "tbl_chat_room_read",
        uniqueConstraints = @UniqueConstraint(columnNames = {"chat_room_id", "user_id"}))
public class ChatRoomRead {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "chat_room_id", nullable = false)
    private Long chatRoomId;

    @Column(name = "creator_id", nullable = false)
    private String creatorId;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Setter
    @Column(name = "last_read_message_id")
    private String lastReadMessageId;

    @CreationTimestamp
    @Column(name = "last_read_at")
    private LocalDateTime lastReadAt;

    @Setter
    @Column(name = "unread_count")
    private Long unreadCount = 0L;
}
