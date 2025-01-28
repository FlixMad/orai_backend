package com.ovengers.userservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_approvals")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Approval {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String approvalId; // UUID로 PK 설정 (자동 생성)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VacationState vacationState; // 승인 상태 (승인됨, 거절됨 등)

    @Column(nullable = false)
    private String title; // 결재 제목

    @Column(length = 500)
    private String contents; // 결재 내용

    @Column(nullable = false)
    private String approvalUserId; // 결재자 (직속 상관) ID

    // vacationId와 연결된 vacation 엔티티를 1:1 관계로 설정
    @OneToOne
    @JoinColumn(name = "vacation_id", referencedColumnName = "vacationId", nullable = false)
    private Vacation vacation; // 연관된 휴가 정보

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt; // 생성일

    @UpdateTimestamp
    private LocalDateTime updatedAt; // 수정일

    // vacationId를 가져오는 메서드 추가
    public String getVacationId() {
        return vacation != null ? vacation.getVacationId() : null;
    }

    // vacationState에 대한 setter 메서드 추가
    public void setState(VacationState vacationState) {
        this.vacationState = vacationState;
    }
}
