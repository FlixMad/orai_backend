package com.ovengers.calendarservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.time.LocalDateTime;
import java.util.UUID;

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
    @Column(name = "schedule_id")
    private UUID scheduleId;

    @NotNull
    private String title;

    private String description;

    @CreatedDate
    @NotNull
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @NotNull
    @Column(name = "start_time")
    private LocalDateTime startTime;

    @NotNull
    @Column(name = "end_time")
    private LocalDateTime endTime;

    @JoinColumn(name = "user_id", nullable = false)
    private String userId;

    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(name = "schedule_status")
    private ScheduleStatus scheduleStatus = ScheduleStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @NotNull
    private Type type;

    public enum ScheduleStatus {
        PENDING, APPROVED, REJECTED, CANCELED
    }

    public enum Type {
        PERSONAL, TEAM
    }

}
