package com.ovengers.calendarservice.repository;

import com.ovengers.calendarservice.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, String> {

    // 특정 사용자의 알림을 조회
    List<Notification> findByUserId(String userId);
}
