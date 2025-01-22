package com.ovengers.etcservice.repository;

import com.ovengers.etcservice.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, String> {

}
