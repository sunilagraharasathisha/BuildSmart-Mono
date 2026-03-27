package com.buildsmart.common.notification;

import com.buildsmart.common.exception.ResourceNotFoundException;
import com.buildsmart.common.notification.dto.NotificationResponse;
import com.buildsmart.common.util.IdGeneratorUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    @Override
    @Transactional
    public void createNotification(String userId, String taskId, String projectId, String message) {
        NotificationEntity last = notificationRepository.findTopByOrderByNotificationIdDesc();
        NotificationEntity notification = new NotificationEntity();
        notification.setNotificationId(IdGeneratorUtil.nextNotificationId(last == null ? null : last.getNotificationId()));
        notification.setUserId(userId);
        notification.setMessage(message);
        notification.setCreatedDate(LocalDateTime.now());
        notification.setRead(false);
        notification.setTaskId(taskId);
        notification.setProjectId(projectId);
        notificationRepository.save(notification);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getNotificationsByUserId(String userId) {
        return notificationRepository.findByUserIdOrderByCreatedDateDesc(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public void markAsRead(String notificationId, String userId) {
        NotificationEntity notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found: " + notificationId));
        if (!notification.getUserId().equals(userId)) {
            throw new ResourceNotFoundException("Notification not found: " + notificationId);
        }
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    private NotificationResponse toResponse(NotificationEntity entity) {
        return new NotificationResponse(
                entity.getNotificationId(),
                entity.getUserId(),
                entity.getMessage(),
                entity.getCreatedDate(),
                entity.isRead(),
                entity.getTaskId(),
                entity.getProjectId()
        );
    }
}
