//package com.ovengers.calendarservice;
//
//import com.ovengers.calendarservice.service.NotificationService;
//import com.ovengers.calendarservice.service.ScheduledNotificationService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.time.LocalDate;
//import java.util.Collections;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//import static org.junit.jupiter.api.Assertions.*;
//
//@ExtendWith(MockitoExtension.class)
//class ScheduledNotificationServiceTest {
//
//	@InjectMocks
//	private ScheduledNotificationService scheduledNotificationService;
//
//	@Mock
//	private NotificationService notificationService;
//
//	@BeforeEach
//	void setUp() {
//		MockitoAnnotations.openMocks(this);
//	}
//
//	/**
//	 * 알림 생성이 정상적으로 수행되는지 검증
//	 */
//	@Test
//	void testGenerateNotificationsForToday() {
//		// Given
//		LocalDate today = LocalDate.now();
//		when(notificationService.generateNotificationsForDateAndReturnTitles(today))
//				.thenReturn(Collections.singletonList("Test Notification"));
//
//		// When
//		scheduledNotificationService.generateNotificationsForToday();
//
//		// Then
//		verify(notificationService, times(1)).generateNotificationsForDateAndReturnTitles(today);
//	}
//
//	/**
//	 * 알림 생성 중 예외가 발생할 때 적절히 처리되는지 검증
//	 */
//	@Test
//	void testGenerateNotificationsWithException() {
//		// Given
//		doThrow(new RuntimeException("Notification generation failed")).when(notificationService)
//				.generateNotificationsForDateAndReturnTitles(any(LocalDate.class));
//
//		// When & Then
//		RuntimeException exception = assertThrows(RuntimeException.class, () -> {
//			scheduledNotificationService.generateNotificationsForToday();
//		});
//
//		assertEquals("Notification generation failed", exception.getMessage());
//		verify(notificationService, times(1)).generateNotificationsForDateAndReturnTitles(any(LocalDate.class));
//	}
//}
