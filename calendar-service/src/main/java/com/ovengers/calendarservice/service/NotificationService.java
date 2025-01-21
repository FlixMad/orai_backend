package com.ovengers.calendarservice.service;

import com.ovengers.calendarservice.entity.Notification;
import com.ovengers.calendarservice.entity.Schedule;
import com.ovengers.calendarservice.repository.CalendarRepository;
import com.ovengers.calendarservice.repository.NotificationRepository;
import com.ovengers.calendarservice.service.CalendarService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
@Transactional
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final CalendarService calendarService;
    private final CalendarRepository calendarRepository;

    /**
     * 특정 날짜에 일정이 있는 사용자에 대해 알림 생성
     * @param date 알림을 생성할 날짜
     * @return 생성된 알림 제목 목록
     */
    public List<String> generateNotificationsForDateAndReturnTitles(LocalDate date) {
        log.info("generateNotificationsForDate started for date: {}", date);

        // 특정 날짜의 일정 조회
        List<Schedule> schedules = calendarRepository.findByDate(date);

        if (schedules.isEmpty()) {
            log.info("No schedules found for date: {}", date);
            return new ArrayList<>(); // 빈 리스트 반환
        }

        List<String> titles = new ArrayList<>(); // 생성된 제목 저장

        for (Schedule schedule : schedules) {
            try {
                Notification notification = Notification.builder()
                        .userId(schedule.getUserId())
                        .title(schedule.getTitle()) // Schedule의 제목을 알림에 복사
                        .message("오늘 일정은 " + schedule.getTitle() + "입니다.")
                        .isRead(false)
                        .createdAt(date.atStartOfDay())
                        .schedule(schedule) // Schedule 엔티티 연결(Optional)
                        .build();

                notificationRepository.save(notification);
                titles.add(schedule.getTitle()); // 제목 리스트에 추가
                log.info("알람 생성: {} with title: {}", schedule.getUserId(), schedule.getTitle());
            } catch (Exception e) {
                log.error("Error while generating notifications for date {}: {}", date, e.getMessage(), e);
                throw new RuntimeException("DB Error", e);
            }
        }

        log.info("generateNotificationsForDate completed for date: {}", date);
        return titles; // 생성된 제목 리스트 반환
    }
}