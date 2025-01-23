package com.ovengers.calendarservice.entity;


import jakarta.persistence.*;
import lombok.*;

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

    @Column(name = "created_at", nullable = false, updatable = false) // 생성 시간
    private LocalDateTime createdAt;

    @Column(name = "is_read", nullable = false) // 읽음 여부
    private boolean isRead;

    @Column(name = "user_id", nullable = false) // 대상 사용자 ID
    private String userId; // 알림 대상 사용자 (외부 서비스와 연계)

    @Column(nullable = false, length = 255)
    private String title;

    // Schedule 과 연관 관계 설정 (Optional)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id", nullable = false)
    private Schedule schedule;


    // 기본값 설정
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now(); // 현재 시간으로 설정
        this.isRead = false; // 기본 읽음 상태는 false
    }

}
