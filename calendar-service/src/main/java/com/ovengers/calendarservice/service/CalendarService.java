package com.ovengers.calendarservice.service;

import com.ovengers.calendarservice.common.auth.TokenUserInfo;
import com.ovengers.calendarservice.dto.request.ScheduleRequestDto;
import com.ovengers.calendarservice.dto.response.ScheduleResponseDto;
import com.ovengers.calendarservice.entity.Department;
import com.ovengers.calendarservice.entity.Schedule;
import com.ovengers.calendarservice.repository.CalendarRepository;
import com.ovengers.calendarservice.repository.DepartmentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CalendarService {

    private final CalendarRepository calendarRepository;
    private final DepartmentRepository departmentRepository;

    // 일정 생성
    public ScheduleResponseDto createSchedule(TokenUserInfo userInfo, ScheduleRequestDto scheduleRequestDto) {
        // Department 조회
        Department department = departmentRepository.findById(userInfo.getDepartmentId())
                .orElseThrow(() -> new RuntimeException("Department not found: " + userInfo.getDepartmentId()));

        Schedule schedule = Schedule.builder()
                .title(scheduleRequestDto.getTitle())
                .description(scheduleRequestDto.getDescription())
                .userId(userInfo.getId())
                .startTime(scheduleRequestDto.getStart())
                .endTime(scheduleRequestDto.getEnd())
                .department(department)
                .build();
        Schedule savedSchedule = calendarRepository.save(schedule);

        return toDto(savedSchedule);
    }

    // 전체 일정 조회
    public List<ScheduleResponseDto> getAllSchedules() {
        return calendarRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    // 부서별 일정 조회
    public List<ScheduleResponseDto> getScheduleByDepartment(String departmentId) {
        return calendarRepository.findByDepartmentId(departmentId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    // 특정 일정 조회 by scheduleId
    public ScheduleResponseDto getScheduleById(String scheduleId) {
        Schedule schedule = calendarRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("Schedule not found"));
        return toDto(schedule);
    }

    // 특정 일정 조회 by userId
    public List<ScheduleResponseDto> getScheduleByUserId(String userId) {
        return calendarRepository.findByUserId(userId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    // 날짜 범위 내 일정 조회
    public List<ScheduleResponseDto> getSchedulesByDateRange(LocalDateTime start, LocalDateTime end) {
        return calendarRepository.findByStartTimeBetween(start, end).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    // 일정 수정
    @Transactional
    public ScheduleResponseDto updateSchedule(String scheduleId, ScheduleRequestDto scheduleRequestDto) {
        Schedule oldSchedule = calendarRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("Schedule not found"));

        // 수정 요청 데이터를 기존 엔티티에 반영
        oldSchedule.setTitle(scheduleRequestDto.getTitle());
        oldSchedule.setDescription(scheduleRequestDto.getDescription());
        oldSchedule.setStartTime(scheduleRequestDto.getStart());
        oldSchedule.setEndTime(scheduleRequestDto.getEnd());

        Schedule updatedSchedule = calendarRepository.save(oldSchedule);
        return toDto(updatedSchedule);
    }

    // 일정 삭제
    public void deleteSchedule(String scheduleId) {
        if (!calendarRepository.existsById(scheduleId)) {
            throw new RuntimeException("Schedule not found for ID: " + scheduleId);
        }
        calendarRepository.deleteById(scheduleId);
    }

    // Schedule 엔티티를 ScheduleResponseDto로 변환하는 메서드
    private ScheduleResponseDto toDto(Schedule schedule) {
        return ScheduleResponseDto.builder()
                .ScheduleId(schedule.getScheduleId().toString())
                .title(schedule.getTitle())
                .description(schedule.getDescription())
                .start(schedule.getStartTime().toString())
                .end(schedule.getEndTime().toString())
                .type(schedule.getType().name())
                .build();
    }

    // 
    public List<String> getUserIdsWithSchedulesForDate(LocalDate date) {
        return null;
    }
}
