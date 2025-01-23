package com.ovengers.calendarservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter @Setter
@Entity
@Builder
@EntityListeners(AuditingEntityListener.class)
@Table(name = "tbl_schedule")
@AllArgsConstructor
@NoArgsConstructor
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "schedule_id", length = 36, nullable = false)
    private String scheduleId;


    @NotNull
    private String title;

    private String description;

    @CreatedDate
    @NotNull
    @Column(name = "created_at")
    private LocalDate createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDate updatedAt;

    @NotNull
    @FutureOrPresent(message = "시작 날짜는 현재 날짜 또는 미래 날짜여야 합니다.")
    @Column(name = "start_time")
    private LocalDate startTime;

    @NotNull
//    @FutureOrPresent(message = "종료 날짜는 시작 날짜 이후여야 합니다.")
    @Column(name = "end_time")
    private LocalDate endTime;

    @AssertTrue(message = "종료 날짜는 시작 날짜 이후여야 합니다.")
    public boolean isEndTimeAfterStartTime() {
        return endTime.isAfter(startTime);
    }

    @JoinColumn(name = "user_id", nullable = false)
    private String userId;

    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(name = "schedule_status")
    @Builder.Default
    private ScheduleStatus scheduleStatus = ScheduleStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @NotNull
    @Builder.Default
    private Type type = Type.TEAM;

    public enum ScheduleStatus {
        PENDING, APPROVED, REJECTED, CANCELED
    }

    public enum Type {
        PERSONAL, TEAM
    }

    @ManyToOne(fetch = FetchType.LAZY) // 부서와 다대일 관계
    @JoinColumn(name = "department_id", nullable = false) // 부서 ID와 조인
    private Department department; // 해당 일정과 연관된 부서

}
