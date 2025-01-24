package com.ovengers.etcservice.entity;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tbl_notification")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "message", nullable = false, length = 255) // 메시지 매핑
    private String message; // 알림 메시지

    // 채팅 서비스에서 맨션 기능을 만들게 된다면 활성화.
//    @Column(name = "url", nullable = false, length = 255) // 메시지 매핑
//    private String url;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false) // 생성 시간
    private LocalDateTime createdAt;

    @Column(name = "is_read", nullable = false) // 읽음 여부
    private boolean isRead;

    @Column(name = "user_id", nullable = false) // 대상 사용자 ID
    private String userId; // 알림 대상 사용자 (외부 서비스와 연계)


}
