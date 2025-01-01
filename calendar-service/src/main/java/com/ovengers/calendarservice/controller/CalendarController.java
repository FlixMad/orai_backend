package com.ovengers.calendarservice.controller;

import com.ovengers.calendarservice.common.CommonResDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/events")
public class CalendarController {

    // 일정조회
    @GetMapping("")
    public CommonResDto getEvents(
            @RequestParam("start") String start,
            @RequestParam("end") String end) {

        Map<String, Objects> response = new HashMap<>();

        return new CommonResDto(HttpStatus.OK, "일정 조회 완료", response);
    }

    // 일정생성
    @PostMapping("")
    public CommonResDto addEvent() {
        Map<String, Object> response = new HashMap<>();

        return new CommonResDto(HttpStatus.OK, "일정 생성 완료", response);

    }

    // 일정수정
    @PutMapping("/{id}")
    public CommonResDto updateEvent() {
        Map<String, Object> response = new HashMap<>();

        return new CommonResDto(HttpStatus.OK, "일정 수정 완료", response);

    }

    // 일정삭제
    @DeleteMapping("/{id}")
    public CommonResDto deleteEvent(@RequestBody CommonResDto commonResDto) {

        return new CommonResDto(HttpStatus.OK, "일정 삭제 완료", commonResDto);

    }
}