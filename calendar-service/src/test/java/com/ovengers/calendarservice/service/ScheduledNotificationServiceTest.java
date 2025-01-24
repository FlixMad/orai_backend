package com.ovengers.calendarservice.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScheduledNotificationServiceTest {

    @InjectMocks
    private ScheduledNotificationService scheduledNotificationService;

    @Mock
    private NotificationService notificationService;

    /**
     * 알림 생성 로직이 정상적으로 동작하는지 테스트
     */
    @Test
    void testGenerateNotificationsForToday() {
        // Given
        LocalDate today = LocalDate.now();

        // When
        scheduledNotificationService.generateNotificationsForToday();

        // Then
        verify(notificationService, times(1)).generateNotificationsForDateAndReturnTitles(today);
    }

    /**
     * 예외가 발생할 경우의 처리 테스트
     */
    @Test
    void testGenerateNotificationsWithException() {
        // Given
        doThrow(new RuntimeException("DB Error")).when(notificationService)
                .generateNotificationsForDateAndReturnTitles(any(LocalDate.class));

        // When & Then
        org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class, () -> {
            scheduledNotificationService.generateNotificationsForToday();
        });

        verify(notificationService, times(1)).generateNotificationsForDateAndReturnTitles(any(LocalDate.class));
    }
}
