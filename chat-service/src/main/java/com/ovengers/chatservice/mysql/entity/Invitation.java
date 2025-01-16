package com.ovengers.chatservice.mysql.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@NoArgsConstructor
@ToString
@AllArgsConstructor
@Builder
@Entity
@Table(name = "tbl_invitation", uniqueConstraints = @UniqueConstraint(columnNames = {"chat_room_id", "user_id"}))
public class Invitation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "chat_room_id", nullable = false)
    private Long chatRoomId;

    @JoinColumn(name = "user_id", nullable = false)
    private String userId;

    @Setter
    private boolean accepted; // 초대 수락 여부
}
