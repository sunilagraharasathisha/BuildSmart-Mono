package com.buildsmart.common.notification;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "notifications")
public class NotificationEntity {

    @Id
    @Column(name = "notification_id", nullable = false, updatable = false, length = 20)
    private String notificationId;

    @Column(name = "user_id", nullable = false, length = 20)
    private String userId;

    @Column(nullable = false, length = 500)
    private String message;

    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;

    @Column(name = "is_read", nullable = false)
    private boolean isRead;

    @Column(name = "task_id", length = 20)
    private String taskId;

    @Column(name = "project_id", length = 20)
    private String projectId;
}
