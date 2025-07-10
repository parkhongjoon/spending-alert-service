package com.zerobase.spendingalertservice.repository;

import com.zerobase.spendingalertservice.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
  List<Notification> findByUserId(Long userId);
}
