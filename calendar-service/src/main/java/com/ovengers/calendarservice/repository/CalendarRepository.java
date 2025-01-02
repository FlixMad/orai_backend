package com.ovengers.calendarservice.repository;

import com.ovengers.calendarservice.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

public interface CalendarRepository extends JpaRepository<Schedule, UUID> {

    // startTime 필드와 endTime 필드를 기준으로 조회
    List<Schedule> findByStartTimeBetween(LocalDateTime start, LocalDateTime end);

}
