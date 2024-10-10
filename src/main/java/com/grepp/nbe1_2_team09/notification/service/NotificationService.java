package com.grepp.nbe1_2_team09.notification.service;

import com.grepp.nbe1_2_team09.notification.controller.dto.NotificationResp;
import com.grepp.nbe1_2_team09.notification.entity.Notification;
import com.grepp.nbe1_2_team09.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Async
    public CompletableFuture<Void> sendNotificationAsync(NotificationResp notificationResp) {
        return CompletableFuture.runAsync(() -> {
            try {
                sendNotification(notificationResp);
            } catch (Exception e) {
                log.error("Failed to send notification: " + e.getMessage(), e);
            }
        });
    }

    public void sendNotification(NotificationResp notificationResp) {
        // 웹소켓을 통해 알림 전송
        messagingTemplate.convertAndSend("/topic/user/" + notificationResp.receiverId(), notificationResp);
        log.info("Sent notification to user: " + notificationResp.receiverId());

        // Redis에 알림 저장
        Notification notification = Notification.builder()
                .id(notificationResp.id())
                .type(notificationResp.type())
                .message(notificationResp.message())
                .senderId(notificationResp.senderId())
                .receiverId(notificationResp.receiverId())
                .createdAt(LocalDateTime.parse(notificationResp.createdAt()))
                .read(notificationResp.read())
                .invitationId(notificationResp.invitationId())
                .build();
        log.info("보낸 사람: " + notificationResp.senderId());
        log.info("Sent notification to user: " + notificationResp.receiverId());
        notificationRepository.save(notification);
    }

    public List<Notification> getNotifications(Long userId, boolean unreadOnly) {
        if (unreadOnly) {
            return notificationRepository.findByReceiverIdAndReadFalse(userId);
        } else {
            return notificationRepository.findByReceiverId(userId);
        }
    }

    public int getUnreadCount(Long userId) {
        return (int) notificationRepository.countByReceiverIdAndReadFalse(userId);
    }

    public void markAsRead(String notificationId, Long userId) {
        Notification notification = notificationRepository.findByIdAndReceiverId(notificationId, userId);
        if (notification != null) {
            notification.setRead(true);
            notificationRepository.save(notification);

            int unreadCount = getUnreadCount(userId);
            messagingTemplate.convertAndSend("/topic/user/" + userId + "/unreadCount", unreadCount);
        } else {
            log.warn("Notification not found or not belonging to user. ID: {}, User: {}", notificationId, userId);
        }
    }

    // 벌크 처리를 사용하여 모든 알림을 읽음 처리
    public void markAllAsRead(Long userId) {
        notificationRepository.markAllAsReadBulk(userId);
        log.info("Marked all notifications as read for user: {}", userId);
        messagingTemplate.convertAndSend("/topic/user/" + userId + "/unreadCount", 0);
    }
}
