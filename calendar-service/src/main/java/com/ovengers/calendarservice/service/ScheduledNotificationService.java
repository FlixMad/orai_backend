////package com.ovengers.calendarservice.service;
////
////import lombok.RequiredArgsConstructor;
////import lombok.extern.slf4j.Slf4j;
////import org.springframework.scheduling.annotation.Scheduled;
////import org.springframework.stereotype.Service;
////
////import java.time.LocalDate;
////
////@Service
////@RequiredArgsConstructor
////@Slf4j
////public class ScheduledNotificationService {
////
////    private final NotificationService notificationService;
////
////    /**
////     * 매주 월요일~금요일 오전 9시에 특정 날짜의 알림을 자동 생성
////     */
////    @Scheduled(cron = "0 0 9 ? * MON-FRI") //
////    public void scheduleDailyNotification() {
////        log.info("Scheduled task started for generating notifications");
////
////        LocalDate today = LocalDate.now();
////
////        try {
////            notificationService.generateNotificationsForDateAndReturnTitles(today);
////            log.info("Notifications generated successfully for {}", today);
////        } catch (Exception e) {
////            log.error("Error occurred while generating notifications for date: {}", today, e);
////        }
////    }
////}
//
//
//package com.ovengers.calendarservice.service;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDate;
//
//@Service
//@RequiredArgsConstructor
//@Slf4j
//public class ScheduledNotificationService {
//
//    private final NotificationService notificationService;
//
//    /**
//     * 매주 월요일~금요일 오전 9시에 특정 날짜의 알림을 자동 생성
//     */
//    @Scheduled(cron = "0 0 9 ? * MON-FRI") // 매주 월~금 오전 9시 실행
//    public void scheduleDailyNotification() {
//        log.info("Scheduled task started for generating notifications");
//        generateNotificationsForToday();
//    }
//
//    /**
//     * 테스트 및 수동 실행을 위한 메서드
//     */
//    public void generateNotificationsForToday() {
//        LocalDate today = LocalDate.now();
//
//        try {
//            notificationService.generateNotificationsForDateAndReturnTitles(today);
//            log.info("Notifications generated successfully for {}", today);
//        } catch (Exception e) {
//            log.error("Error occurred while generating notifications for date: {}", today, e);
//            throw new RuntimeException("Notification generation failed", e);
//        }
//    }
//}

//
//
//package com.ovengers.calendarservice.service;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDate;
//
//@Service
//@RequiredArgsConstructor
//@Slf4j
//public class ScheduledNotificationService {
//
//
//    /**
//     * 매주 월요일~금요일 오전 9시에 특정 날짜의 알림을 자동 생성
//     */
//    @Scheduled(cron = "0 0 9 ? * MON-FRI") // 매주 월~금 오전 9시 실행
//    public void scheduleDailyNotification() {
//        log.info("Scheduled task started for generating notifications");
//        generateNotificationsForToday();
//    }
//
//    /**
//     * 테스트 및 수동 실행을 위한 메서드
//     */
//    public void generateNotificationsForToday() {
//        LocalDate today = LocalDate.now();
//
//        try {
//            notificationService.generateNotificationsForDateAndReturnTitles(today);
//            log.info("Notifications generated successfully for {}", today);
//        } catch (Exception e) {
//            log.error("Error occurred while generating notifications for date: {}", today, e);
//            throw new RuntimeException("Notification generation failed", e);
//        }
//    }
//}
