package com.sunrisedental.controller;

import com.sunrisedental.dto.NotificationResponse;
import com.sunrisedental.service.NotificationService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "*", maxAge = 3600)
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(
            NotificationService notificationService) {

        this.notificationService = notificationService;
    }

    // =========================================================
    // GET ALL MY NOTIFICATIONS
    // =========================================================

    @GetMapping
    public ResponseEntity<List<NotificationResponse>>
    getMyNotifications(
            Authentication authentication) {

        return ResponseEntity.ok(
                notificationService.getMyNotifications(
                        authentication.getName()
                )
        );
    }

    // =========================================================
    // GET UNREAD NOTIFICATIONS
    // =========================================================

    @GetMapping("/unread")
    public ResponseEntity<List<NotificationResponse>>
    getUnreadNotifications(
            Authentication authentication) {

        return ResponseEntity.ok(
                notificationService.getMyUnreadNotifications(
                        authentication.getName()
                )
        );
    }

    // =========================================================
    // GET UNREAD COUNT
    // =========================================================

    @GetMapping("/unread/count")
    public ResponseEntity<Long>
    getUnreadCount(
            Authentication authentication) {

        return ResponseEntity.ok(
                notificationService.getUnreadCount(
                        authentication.getName()
                )
        );
    }

    // =========================================================
    // MARK ONE AS READ
    // =========================================================

    @PutMapping("/{notificationId}/read")
    public ResponseEntity<NotificationResponse>
    markAsRead(
            @PathVariable Long notificationId,
            Authentication authentication) {

        return ResponseEntity.ok(
                notificationService.markAsRead(
                        notificationId,
                        authentication.getName()
                )
        );
    }

    // =========================================================
    // MARK ALL AS READ
    // =========================================================

    @PutMapping("/read-all")
    public ResponseEntity<String>
    markAllAsRead(
            Authentication authentication) {

        notificationService.markAllAsRead(
                authentication.getName()
        );

        return ResponseEntity.ok(
                "All notifications marked as read."
        );
    }

    // =========================================================
    // DELETE NOTIFICATION
    // =========================================================

    @DeleteMapping("/{notificationId}")
    public ResponseEntity<String>
    deleteNotification(
            @PathVariable Long notificationId,
            Authentication authentication) {

        notificationService.deleteNotification(
                notificationId,
                authentication.getName()
        );

        return ResponseEntity.ok(
                "Notification deleted successfully."
        );
    }
}
