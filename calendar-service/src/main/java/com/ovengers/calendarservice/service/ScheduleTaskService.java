package com.ovengers.calendarservice.service;

import com.ovengers.calendarservice.entity.Schedule;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import com.ovengers.calendarservice.repository.CalendarRepository;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduleTaskService {

    private final CalendarRepository calendarRepository;
    private final CalendarService calendarService;

    @Scheduled(cron = "0 0 8 * * MON-FRI") // 평일(월~금) 오전 8시 실행
//    @Scheduled(fixedRate = 60000) // 테스트용 (1분마다 실행)
    public void checkDailySchedules() {
        LocalDate today = LocalDate.now();
        System.out.println("스케줄 조회 작업 실행: " + today);

        // 기존 CalendarRepository를 사용하여 해당 날짜의 일정 조회
        List<Schedule> schedules = calendarRepository.findByStartTime(today);
        schedules.forEach(calendarService::createNotification);

        if (schedules.isEmpty()) {
            System.out.println("오늘 일정이 없습니다.");
        } else {
            schedules.forEach(schedule ->
                    System.out.println("일정: " + schedule.getTitle() + " - " + schedule.getStartTime())
            );
        }

        System.out.println("스케줄 조회 완료.");
    }
}
