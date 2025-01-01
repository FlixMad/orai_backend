package com.ovengers.calendarservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter @Setter
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "schedules")
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID scheduleId;

    @NotNull
    private String title;

    private String description;

    @CreatedDate
    @NotNull
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @NotNull
    private LocalDateTime startTime;

    @NotNull
    private LocalDateTime endTime;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @NotNull
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
