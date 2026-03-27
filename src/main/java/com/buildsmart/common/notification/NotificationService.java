package com.buildsmart.common.notification;

import com.buildsmart.common.notification.dto.NotificationResponse;

import java.util.List;

public interface NotificationService {
    void createNotification(String userId, String taskId, String projectId, String message);
    List<NotificationResponse> getNotificationsByUserId(String userId);
    void markAsRead(String notificationId, String userId);
}
