package com.ovengers.userservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_attitude")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Attitude {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "attitude_id")
    private String attitudeId; // 근태 아이디

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createdAt; // 생성일자

    @Column(nullable = false, name = "check_in_time")
    private LocalDateTime checkInTime; // 출근 시간

    @Column(nullable = true, name = "check_out_time")
    private LocalDateTime checkOutTime; // 퇴근 시간

    // User와의 관계 (N:1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}

