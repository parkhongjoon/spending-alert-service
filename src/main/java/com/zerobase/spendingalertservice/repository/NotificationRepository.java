package com.zerobase.spendingalertservice.repository;

import com.zerobase.spendingalertservice.domain.Notification;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

  List<Notification> findByUserId(Long userId);
}
