package com.buildsmart.common.notification;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<NotificationEntity, String> {
    NotificationEntity findTopByOrderByNotificationIdDesc();
    List<NotificationEntity> findByUserIdOrderByCreatedDateDesc(String userId);
}
