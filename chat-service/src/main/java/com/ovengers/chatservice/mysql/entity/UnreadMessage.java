package com.ovengers.chatservice.mysql.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@ToString
@AllArgsConstructor
@Builder
@Entity
@Table(name = "tbl_unread_count")
public class UnreadMessage {

    @Id
    @Column(name = "uread_count_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long unreadCountId;

    @Column(name = "unread_count")
    private Long unreadCount;

    @Column(name = "chat_room_id")
    private Long chatRoomId;

    @Column(name = "user_id")
    private String userId;

}
