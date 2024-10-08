package com.grepp.nbe1_2_team09.notification.service;

import com.grepp.nbe1_2_team09.notification.controller.dto.NotificationDto;
import com.grepp.nbe1_2_team09.notification.entity.Notification;
import com.grepp.nbe1_2_team09.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public void sendNotification(NotificationDto notificationDto) {
        // 실시간 메시지 전송
        messagingTemplate.convertAndSend("/topic/user/" + notificationDto.receiverId(), notificationDto);
        log.info("Sent notification to user: " + notificationDto.receiverId());

        // Redis에 알림 저장
        Notification notification = Notification.builder()
                .id(UUID.randomUUID().toString())
                .type(notificationDto.type())
                .message(notificationDto.message())
                .senderId(notificationDto.senderId())
                .receiverId(notificationDto.receiverId())
                .createdAt(LocalDateTime.now())
                .read(false)
                .build();
        log.info("보낸 사람: " + notificationDto.senderId());
        log.info("Sent notification to user: " + notificationDto.receiverId());
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
        } else {
            log.warn("Notification not found or not belonging to user. ID: {}, User: {}", notificationId, userId);
        }
    }

    public void markAllAsRead(Long userId) {
        List<Notification> unreadNotifications = notificationRepository.findByReceiverIdAndReadFalse(userId);
        unreadNotifications.forEach(notification -> {
            notification.setRead(true);
            notificationRepository.save(notification);
        });
    }
}