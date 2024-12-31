package com.ovengers.calendarservice.controller;

import com.ovengers.calendarservice.common.CommonResDto;
import com.ovengers.calendarservice.dto.request.ScheduleRequestDto;
import com.ovengers.calendarservice.dto.response.ScheduleResponseDto;
import com.ovengers.calendarservice.entity.Schedule;
import com.ovengers.calendarservice.service.CalendarService;
import lombok.RequiredArgsConstructor;
import org.springframework.cglib.core.Local;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/schedules")
@RequiredArgsConstructor
public class CalendarController {

    private final CalendarService calendarService;

    // 전체 일정 조회
    @GetMapping("")
    public ResponseEntity<List<ScheduleResponseDto>> getAllSchedules(
            @RequestParam("start")LocalDateTime start,
            @RequestParam("end")LocalDateTime end) {
        List<ScheduleResponseDto> schedules = calendarService.getAllSchedules();
        return ResponseEntity.ok(schedules);
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

    // 일정 삭제
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<CommonResDto> deleteSchedule(@PathVariable("id") UUID scheduleId) {
        calendarService.deleteSchedule(scheduleId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }



}