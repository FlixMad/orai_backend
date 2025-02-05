package com.ovengers.calendarservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;

@Getter
@Setter
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
    @JoinColumn(name = "user_id", nullable = false)
    private String userId;

    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(name = "schedule_status")
    @Builder.Default
    private ScheduleStatus scheduleStatus = ScheduleStatus.UPCOMING;

    @Enumerated(EnumType.STRING)
    @NotNull
    @Builder.Default
    private Type type = Type.TEAM;

    @ManyToOne(fetch = FetchType.LAZY) // 부서와 다대일 관계
    @JoinColumn(name = "department_id", nullable = false) // 부서 ID와 조인
    private Department department; // 해당 일정과 연관된 부서

    @AssertTrue(message = "종료 날짜는 시작 날짜 이후여야 합니다.")
    public boolean isEndTimeAfterStartTime() {
        return endTime.isAfter(startTime);
    }

    // 기본 상태 설정 로직
    @PrePersist
    protected void onCreate() {
        if (scheduleStatus == null) {
            this.scheduleStatus = ScheduleStatus.UPCOMING;
        }
    }

    public enum ScheduleStatus {
        UPCOMING, IN_PROGRESS, COMPLETED
    }

    public enum Type {
        TEAM, DIVISION, GROUP
    }

}
