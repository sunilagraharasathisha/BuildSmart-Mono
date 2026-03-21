package com.buildsmart.common.notification;

import com.buildsmart.common.exception.ResourceNotFoundException;
import com.buildsmart.common.notification.dto.NotificationResponse;
import com.buildsmart.iam.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Tag(name = "Notification APIs", description = "User notification endpoints")
public class NotificationController {

    private final NotificationService notificationService;
    private final UserService userService;

    @GetMapping
    @Operation(summary = "Get my notifications", description = "Returns notifications for the logged-in user (extracted from JWT)")
    @ApiResponse(responseCode = "200", description = "Notifications fetched")
    public ResponseEntity<List<NotificationResponse>> getMyNotifications() {
        String userId = getCurrentUserId();
        return ResponseEntity.ok(notificationService.getNotificationsByUserId(userId));
    }

    @PatchMapping("/{notificationId}/read")
    @Operation(summary = "Mark notification as read")
    @ApiResponse(responseCode = "200", description = "Notification marked as read")
    public ResponseEntity<Void> markAsRead(@PathVariable String notificationId) {
        String userId = getCurrentUserId();
        notificationService.markAsRead(notificationId, userId);
        return ResponseEntity.ok().build();
    }

    private String getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            throw new ResourceNotFoundException("User not found in session");
        }
        String email = auth.getName();
        return userService.findByEmail(email)
                .map(user -> user.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));
    }
}
