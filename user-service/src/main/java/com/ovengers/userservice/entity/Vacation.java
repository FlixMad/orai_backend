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
    private Long vacationId; // 연차 아이디

    @Column(nullable = false)
    private String type; // 연차 유형 (ex: 연차, 반차 등)

    @Column(nullable = false)
    private LocalDateTime startDate; // 연차 시작일

    @Column(nullable = false)
    private LocalDateTime endDate; // 연차 종료일

    @Column(nullable = false)
    private LocalDateTime createdAt; // 연차 생성일자

    private LocalDateTime updatedAt; // 연차 수정일자

    @Column(nullable = false)
    private Boolean vacationPermission; // 연차 허가 여부 (true: 허가, false: 미허가)

    @Column(nullable = false)
    private Long userId; // 사용자 아이디 (외래키)
}
