package com.ovengers.calendarservice.repository;

import com.ovengers.calendarservice.dto.response.ScheduleResponseDto;
import com.ovengers.calendarservice.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

public interface CalendarRepository extends JpaRepository<Schedule, String> {

    // startTime 필드와 endTime 필드를 기준으로 조회
    List<Schedule> findByStartTimeBetween(LocalDateTime start, LocalDateTime end);

    // 부서별 일정 조회
    @Query("SELECT s FROM Schedule s WHERE s.department.departmentId = :departmentId")
    List<Schedule> findByDepartmentId(String departmentId);

    // 오늘 일정과 관련된 userId로 조회
    @Query("SELECT DISTINCT s.userId FROM Schedule s WHERE s.startTime = :today")
    List<String> findDistinctUserIdForToday(LocalDate today);

    List<Schedule> findByUserId(String userId);

    @Query("SELECT s FROM Schedule s WHERE DATE(s.startTime) = :date")
    List<Schedule> findByDate(LocalDate date);

}
