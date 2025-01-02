package com.ovengers.calendarservice.service;

import com.ovengers.calendarservice.dto.request.ScheduleRequestDto;
import com.ovengers.calendarservice.dto.response.ScheduleResponseDto;
import com.ovengers.calendarservice.entity.Schedule;
import com.ovengers.calendarservice.repository.CalendarRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CalendarService {

    private final CalendarRepository calendarRepository;

    // 일정 생성
    public ScheduleResponseDto createSchedule(ScheduleRequestDto scheduleRequestDto) {
        Schedule schedule = Schedule.builder()
                .title(scheduleRequestDto.getTitle())
                .startTime(scheduleRequestDto.getStart())
                .endTime(scheduleRequestDto.getEnd())
                .build();
        Schedule savedSchedule = calendarRepository.save(schedule);

        return ScheduleResponseDto.builder()
                .userId(savedSchedule.getUserId())
                .title(savedSchedule.getTitle())
                .start(savedSchedule.getStartTime().toString())
                .end(savedSchedule.getEndTime().toString())
                .type(savedSchedule.getType().name())
                .build();
    }

    // 일정 조회
    // 전체 일정 조회
    // Schedule 엔티티 리스트를 ScheduleResponseDto 리스트로 변환
    public List<ScheduleResponseDto> getAllSchedules() {
        return calendarRepository.findAll().stream()
                .map(schedule -> ScheduleResponseDto.builder()
                        .userId(schedule.getUserId())
                        .title(schedule.getTitle())
                        .start(schedule.getStartTime().toString())
                        .end(schedule.getEndTime().toString())
                        .type(schedule.getType().name())
                        .build())
                .collect(Collectors.toList());
    }

    // 특정 일정 조회
    public ScheduleResponseDto getScheduleById(UUID scheduleId) {

        Schedule schedule = calendarRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("Schedule not found"));

        return ScheduleResponseDto.builder()
                .userId(schedule.getUserId())
                .title(schedule.getTitle())
                .start(schedule.getStartTime().toString())
                .end(schedule.getEndTime().toString())
                .build();
    }

    public List<ScheduleResponseDto> getSchedulesByDateRange(LocalDateTime start, LocalDateTime end) {
        return calendarRepository.findByStartTimeBetween(start, end).stream()
                .map(schedule -> ScheduleResponseDto.builder()
                        .userId(schedule.getUserId())
                        .title(schedule.getTitle())
                        .start(schedule.getStartTime().toString())
                        .end(schedule.getEndTime().toString())
                        .type(schedule.getType().name())
                        .build())
                .collect(Collectors.toList());
    }


    // 일정 수정
    @Transactional
    public ScheduleResponseDto updateSchedule(UUID scheduleId, ScheduleRequestDto scheduleRequestDto) {

        Schedule oldSchedule = calendarRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("Schedule not found"));

        // 수정 요청 데이터를 기존 엔티티에 반영
        oldSchedule.setTitle(scheduleRequestDto.getTitle());
        oldSchedule.setStartTime(scheduleRequestDto.getStart());
        oldSchedule.setEndTime(scheduleRequestDto.getEnd());

        Schedule updatedSchedule = calendarRepository.save(oldSchedule);

        return ScheduleResponseDto.builder()
                .userId(updatedSchedule.getUserId())
                .title(updatedSchedule.getTitle())
                .start(updatedSchedule.getStartTime().toString())
                .end(updatedSchedule.getEndTime().toString())
                .build();
    }

    // 일정 삭제
    public void deleteSchedule(UUID scheduleId) {

        if (!calendarRepository.existsById(scheduleId)) {
            throw new RuntimeException("Schedule not found");
        }
        calendarRepository.deleteById(scheduleId);
    }


}
