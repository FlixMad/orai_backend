package com.ovengers.userservice.controllers;

import com.ovengers.userservice.dto.VacationRequestDto;
import com.ovengers.userservice.dto.VacationResponseDto;
import com.ovengers.userservice.service.VacationService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/vacations")
@Slf4j
public class VacationController {

    private final VacationService vacationService;

    @PostMapping("/apply")
    public ResponseEntity<VacationResponseDto> applyForVacation(@RequestBody VacationRequestDto requestDto) {
        log.info("/api/vacations/apply: POST, dto: {}", requestDto);
        VacationResponseDto responseDto = vacationService.applyForVacation(requestDto);
        return ResponseEntity.ok(responseDto);
    }
    // 특정 사용자의 휴가 내역 조회 엔드포인트 추가
    @GetMapping("/{userId}")
    public ResponseEntity<List<VacationResponseDto>> getUserVacations(@PathVariable String userId) {
        log.info("/api/vacations/{}: GET", userId);
        List<VacationResponseDto> vacations = vacationService.findVacationsByUserId(userId);
        return ResponseEntity.ok(vacations);
    }
}
