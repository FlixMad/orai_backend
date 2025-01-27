package com.ovengers.userservice.entity;

import com.ovengers.userservice.entity.Approval;
import com.ovengers.userservice.entity.VacationState;
import com.ovengers.userservice.entity.VacationType;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "tbl_vacations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Vacation {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String vacationId; // UUID로 PK 설정
    @Column(nullable = false)
    private String title; // 제목

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VacationType type; // 휴가 유형 (예: 연차, 병가 등)

    @Column(nullable = false)
    private LocalDateTime startDate; // 휴가 시작일

    @Column(nullable = false)
    private LocalDateTime endDate; // 휴가 종료일

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VacationState vacationState; // 휴가 상태 (진행중, 승인됨, 거절됨)

    @Column(nullable = false)
    private String userId; // 휴가 신청자 ID

    @OneToOne(mappedBy = "vacation", cascade = CascadeType.ALL)
    @JoinColumn(name = "vacation_id", referencedColumnName = "vacationId") // 외래 키 설정
    private Approval approval; // 연관된 결재 정보

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt; // 생성일

    @UpdateTimestamp
    private LocalDateTime updatedAt; // 수정일


}
