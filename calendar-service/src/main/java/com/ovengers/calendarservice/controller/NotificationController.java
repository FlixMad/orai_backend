package com.ovengers.calendarservice.controller;

import com.ovengers.calendarservice.common.auth.TokenUserInfo;
import com.ovengers.calendarservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/notifications")
@Slf4j
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * 특정 날짜에 대해 알림을 생성하는 엔드포인트
     * @param date 알림을 생성할 날짜 (ISO 형식 yyyy-MM-dd)
     * @return 성공 또는 실패 메시지
     */
    @PostMapping("/")
    public ResponseEntity<Map<String, Object>> generateNotifications(@RequestParam("date") String date) {
        log.info("generateNotifications endpoint called with date: {}", date);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof TokenUserInfo)) {
            log.warn("Unauthorized access attempt");
            Map<String, Object> response = new HashMap<>();
            response.put("error", "Unauthorized: Invalid authentication");
            return ResponseEntity.status(401).body(response);
        }

        TokenUserInfo userInfo = (TokenUserInfo) authentication.getPrincipal();
        log.info("Authenticated user: {}", userInfo.getId());

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate localDate = LocalDate.parse(date, formatter);

            // 알림 생성 호출 (제목 리스트 반환)
            List<String> titles = notificationService.generateNotificationsForDateAndReturnTitles(localDate);
            log.info("Notifications successfully generated for date: {}", date);

            // JSON 응답 반환
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Notifications generated successfully");
            response.put("date", date);
            response.put("titles", titles);
            return ResponseEntity.ok(response);
        } catch (DateTimeParseException e) {
            log.error("Invalid date format: {}", date, e);
            Map<String, Object> response = new HashMap<>();
            response.put("error", "Invalid date format. Please use 'yyyy-MM-dd'.");
            return ResponseEntity.status(400).body(response);
        } catch (Exception e) {
            log.error("Failed to generate notifications for date: {}", date, e);
            Map<String, Object> response = new HashMap<>();
            response.put("error", "Failed to generate notifications.");
            response.put("details", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

}
