package com.ovengers.calendarservice.controller;

import com.ovengers.calendarservice.common.CommonResDto;
import com.ovengers.calendarservice.dto.request.ScheduleRequestDto;
import com.ovengers.calendarservice.dto.response.ScheduleResponseDto;
import com.ovengers.calendarservice.entity.Schedule;
import com.ovengers.calendarservice.service.CalendarService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.core.Local;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/schedules")
@RequiredArgsConstructor
@Slf4j
public class CalendarController {

    private final CalendarService calendarService;

    // 전체 일정 조회
    @GetMapping("")
    public ResponseEntity<List<ScheduleResponseDto>> getAllSchedules() {
        try {
            List<ScheduleResponseDto> schedules = calendarService.getAllSchedules();
            return ResponseEntity.ok(schedules);
        } catch (Exception e) {
            log.error("Error fetching schedules", e); // 예외 로그 기록
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.emptyList());
        }
    }

    // 특정 일정 조회
    @GetMapping("/{id}")
    public ResponseEntity<ScheduleResponseDto> getScheduleById(@PathVariable("id") UUID scheduleId) {
        ScheduleResponseDto schedule = calendarService.getScheduleById(scheduleId);
        return ResponseEntity.ok(schedule);
    }


    // 일정 생성
    @PostMapping("/create-schedule")
    public ResponseEntity<ScheduleResponseDto> addSchedule(@RequestBody ScheduleRequestDto scheduleRequestDto) {
        log.info("/create-schedule: POST!, dto: {}", scheduleRequestDto);
        ScheduleResponseDto createdSchedule = calendarService.createSchedule(scheduleRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdSchedule);
    }

    // 일정 수정
    @PutMapping("/modify-schedule/{id}")
    public ResponseEntity<ScheduleResponseDto> modifySchedule(
            @RequestBody ScheduleRequestDto scheduleRequestDto,
            @PathVariable("id") UUID scheduleId) {

        ScheduleResponseDto modifySchedule = calendarService.updateSchedule(scheduleId, scheduleRequestDto);

        return ResponseEntity.ok(modifySchedule);
    }

    @DeleteMapping("/delete-schedule")
    public ResponseEntity<Void> deleteSchedule(@RequestParam UUID scheduleId) {
        if (scheduleId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "scheduleId is required");
        }

        calendarService.deleteSchedule(scheduleId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}