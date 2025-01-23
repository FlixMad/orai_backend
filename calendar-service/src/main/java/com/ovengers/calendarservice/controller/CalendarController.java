package com.ovengers.calendarservice.controller;

import com.ovengers.calendarservice.common.CommonResDto;
import com.ovengers.calendarservice.common.auth.TokenUserInfo;
import com.ovengers.calendarservice.dto.request.ScheduleRequestDto;
import com.ovengers.calendarservice.dto.response.ScheduleResponseDto;
import com.ovengers.calendarservice.entity.Schedule;
import com.ovengers.calendarservice.service.CalendarService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.service.SecurityService;
import org.springframework.cglib.core.Local;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

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
    public ResponseEntity<List<ScheduleResponseDto>> getAllSchedules(@AuthenticationPrincipal TokenUserInfo info) {
        String departmentId = info.getDepartmentId(); // NullPointerException 방지

        log.info("TokenUserInfo: {}", info);

        List<ScheduleResponseDto> schedules;
        if ("team9".equals(departmentId)) {
            try {
                schedules = calendarService.getAllSchedules();
            } catch (Exception e) {
                log.error("Error fetching schedules", e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
            }
        } else {
            schedules = calendarService.getScheduleByDepartment(departmentId);
        }

        log.info("schedules: {}", schedules);

        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(schedules);
    }
    // 특정 일정 조회
    @GetMapping("/{id}")
    public ResponseEntity<ScheduleResponseDto> getScheduleById(@PathVariable("id") String scheduleId) {
        ScheduleResponseDto schedule = calendarService.getScheduleById(scheduleId);
        return ResponseEntity.ok(schedule);
    }


    // 일정 생성
    @PostMapping("/create-schedule")
    public ResponseEntity<ScheduleResponseDto> addSchedule(
            @RequestBody ScheduleRequestDto scheduleRequestDto,
            @AuthenticationPrincipal TokenUserInfo userInfo) {
        log.info("User Info: {}", userInfo); // 사용자 정보 확인
        log.info("ScheduleRequestDto: {}", scheduleRequestDto);


        ScheduleResponseDto createdSchedule = calendarService.createSchedule(userInfo, scheduleRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdSchedule);
    }

    // 일정 수정
    @PutMapping("/modify-schedule/{id}")
    public ResponseEntity<ScheduleResponseDto> modifySchedule(
            @RequestBody ScheduleRequestDto scheduleRequestDto,
            @PathVariable("id") String scheduleId) {

        ScheduleResponseDto modifySchedule = calendarService.updateSchedule(scheduleId, scheduleRequestDto);

        return ResponseEntity.ok(modifySchedule);
    }

    @DeleteMapping("/delete-schedule")
    public ResponseEntity<Void> deleteSchedule(@RequestParam String scheduleId) {
        if (scheduleId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "scheduleId is required");
        }

        calendarService.deleteSchedule(scheduleId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}