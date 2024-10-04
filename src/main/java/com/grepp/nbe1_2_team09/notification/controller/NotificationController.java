package com.grepp.nbe1_2_team09.notification.controller;

import com.grepp.nbe1_2_team09.admin.service.CustomUserDetails;
import com.grepp.nbe1_2_team09.notification.controller.dto.NotificationDto;
import com.grepp.nbe1_2_team09.notification.entity.Notification;
import com.grepp.nbe1_2_team09.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @MessageMapping("/invite")
    public void sendInvitation(NotificationDto notificationDto) {
        notificationService.sendNotification(notificationDto);
    }

    @MessageMapping("/inviteResponse")
    public void sendInvitationResponse(NotificationDto notificationDto) {
        notificationService.sendNotification(notificationDto);
    }

    @GetMapping("/notifications")
    public ResponseEntity<List<Notification>> getNotifications(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(defaultValue = "false") boolean unreadOnly) {
        Long userId = userDetails.getUser().getUserId();
        return ResponseEntity.ok(notificationService.getNotifications(userId, unreadOnly));
    }

    @GetMapping("/notifications/unread-count")
    public ResponseEntity<Integer> getUnreadCount(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUser().getUserId();
        return ResponseEntity.ok(notificationService.getUnreadCount(userId));
    }

    @PostMapping("/notifications/{notificationId}/read")
    public ResponseEntity<Void> markNotificationAsRead(
            @PathVariable String notificationId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUser().getUserId();
        notificationService.markAsRead(notificationId, userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/notifications/mark-all-read")
    public ResponseEntity<Void> markAllNotificationsAsRead(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUser().getUserId();
        notificationService.markAllAsRead(userId);
        return ResponseEntity.ok().build();
    }

}
