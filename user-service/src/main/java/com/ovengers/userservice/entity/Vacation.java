package com.ovengers.userservice.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "vacation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vacation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vacation_id")
    private Long vacationId; // 연차 아이디

    @Column(nullable = false)
    private String type; // 연차 유형 (예: 연차, 반차 등)

    @Column(nullable = false)
    private LocalDateTime startDate; // 연차 시작일

    @Column(nullable = false)
    private LocalDateTime endDate; // 연차 종료일

    @Column(nullable = false)
    private LocalDateTime createdAt; // 생성일자

    private LocalDateTime updatedAt; // 수정일자

    @Column(nullable = false)
    private Boolean vacationPermission; // 연차 승인 여부

    // User와의 관계 (N:1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
