package com.buildsmart.common.notification.dto;

import java.time.LocalDateTime;

public record NotificationResponse(
        String notificationId,
        String userId,
        String message,
        LocalDateTime createdDate,
        boolean isRead,
        String taskId,
        String projectId
) {
}
