package com.ovengers.userservice.controllers;

import com.ovengers.userservice.service.AttitudeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/attitude")
@Service
public class AttitudeController {

    private final AttitudeService attitudeService;

    @Autowired
    public AttitudeController(AttitudeService attitudeService) {
        this.attitudeService = attitudeService;
    }

    // 출근 기록 API
    @PostMapping("/checkin")
    public ResponseEntity<?> recordCheckIn(@RequestBody Map<String, String> request) {
        String userId = request.get("userId"); // 여기서 userId를 받습니다.
        return ResponseEntity.ok(attitudeService.recordCheckIn(userId));
    }

    // 퇴근 기록 API
    @PostMapping("/checkout")
    public ResponseEntity<?> recordCheckOut(@RequestBody Map<String, String> request) {
        String userId = request.get("userId"); // userId를 받아서 처리
        return ResponseEntity.ok(attitudeService.recordCheckOut(userId));
    }
}
