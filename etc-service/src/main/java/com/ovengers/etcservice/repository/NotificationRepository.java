package com.ovengers.etcservice.repository;

import com.ovengers.etcservice.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, String> {

    List<Notification> findAllByUserId(String userId);

    long countByUserIdAndIsReadFalse(String userId);
}
