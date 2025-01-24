package com.ovengers.userservice.entity;

import jakarta.persistence.*;
import lombok.*;

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
    private String approvalId; // UUID로 PK 설정// PK (자동 생성)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VacationState status; // 결재 상태 (승인됨, 거절됨 등)

    @Column(nullable = false)
    private String title; // 결재 제목

    @Column(length = 500)
    private String contents; // 결재 내용

    @Column(nullable = false)
    private String approvalUserId; // 결재자 (직속 상관) ID

    @OneToOne
    @JoinColumn(name = "vacation_id", nullable = false)
    private Vacation vacation; // 연관된 휴가 정보

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt; // 생성일

    private LocalDateTime updatedAt; // 수정일


}
