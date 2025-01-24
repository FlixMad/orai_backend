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
import org.springframework.web.multipart.MultipartFile;
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
        if (info == null || info.getDepartmentId() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "유효하지 않은 사용자입니다.");
        }

        String departmentId = info.getDepartmentId();
        log.info("TokenUserInfo: {}", info);

        List<ScheduleResponseDto> schedules;

        try {
            if ("team9".equals(departmentId)) {
                // 'team9'이면 전체 일정 조회
                schedules = calendarService.getAllSchedules();
            } else {
                // 사용자의 팀 및 상위 부서 일정 조회
                schedules = calendarService.getSchedulesForUser(departmentId);
            }
        } catch (Exception e) {
            log.error("Error fetching schedules for department: {}", departmentId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }

        log.info("Fetched schedules: {}", schedules.size());

        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(schedules);
    }

//    // 특정 일정 조회
//    @GetMapping("/{id}")
//    public ResponseEntity<ScheduleResponseDto> getScheduleById(@PathVariable("id") String scheduleId) {
//        ScheduleResponseDto schedule = calendarService.getScheduleById(scheduleId);
//        return ResponseEntity.ok(schedule);
//    }


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

/*
    // 첨부파일 관련 메서드

    @PostMapping("/upload")
    public ResponseEntity<String> uploadAttachment(@RequestParam("file") MultipartFile file) {
        try {
//            String fileUrl = calendarService.(file);
            return ResponseEntity.ok(fileUrl);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("파일 업로드 실패: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteAttachment(@RequestParam("fileUrl") String fileUrl) {
        try {
//            calendarService.(fileUrl);
            return ResponseEntity.ok("파일 삭제 성공");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("파일 삭제 실패: " + e.getMessage());
        }
    }
*/
}