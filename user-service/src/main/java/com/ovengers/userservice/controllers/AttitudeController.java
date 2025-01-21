package com.ovengers.userservice.controllers;

import com.ovengers.userservice.dto.AttitudeResponseDto;
import com.ovengers.userservice.service.AttitudeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/attitude")
@RequiredArgsConstructor
public class AttitudeController {

    private final AttitudeService attitudeService;

    // 출근 기록 API
    @PostMapping("/checkin")
    public ResponseEntity<AttitudeResponseDto> checkIn() {
        AttitudeResponseDto response = attitudeService.recordCheckIn();  // 인자 없이 호출
        return ResponseEntity.ok(response);
    }

    // 퇴근 기록 API
    @PostMapping("/checkout")
    public ResponseEntity<AttitudeResponseDto> checkOut() {
        AttitudeResponseDto response = attitudeService.recordCheckOut();  // 인자 없이 호출
        return ResponseEntity.ok(response);
    }
}
