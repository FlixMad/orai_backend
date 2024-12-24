package com.ovengers.userservice.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "attitude")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Attitude {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long attitudeId; // 근태 아이디

    @Column(nullable = false)
    private LocalDateTime createdAt; // 근태 생성일자

    @Column(nullable = false)
    private LocalDateTime checkInTime; // 출근 시간

    @Column(nullable = false)
    private LocalDateTime checkOutTime; // 퇴근 시간

    @Column(nullable = false)
    private Long userId; // 사용자 아이디 (외래키)
}
