package com.grepp.nbe1_2_team09.notification.repository;

import com.grepp.nbe1_2_team09.notification.entity.Notification;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class NotificationRepository {

    private static final String KEY_PREFIX = "notification:user:";

    private final RedisTemplate<String, Notification> notificationRedisTemplate;

    public NotificationRepository(@Qualifier("notificationRedisTemplate") RedisTemplate<String, Notification> notificationRedisTemplate) {
        this.notificationRedisTemplate = notificationRedisTemplate;
    }

    public void save(Notification notification) {
        String key = KEY_PREFIX + notification.getReceiverId();
        notificationRedisTemplate.opsForHash().put(key, notification.getId(), notification);
    }

    public Notification findByIdAndReceiverId(String notificationId, Long receiverId) {
        String key = KEY_PREFIX + receiverId;
        return (Notification) notificationRedisTemplate.opsForHash().get(key, notificationId);
    }

    public List<Notification> findByReceiverIdAndReadFalse(Long receiverId) {
        String key = KEY_PREFIX + receiverId;
        return notificationRedisTemplate.opsForHash().values(key).stream()
                .map(obj -> (Notification) obj)
                .filter(n -> !n.isRead())
                .collect(Collectors.toList());
    }

    public List<Notification> findByReceiverId(Long receiverId) {
        String key = KEY_PREFIX + receiverId;
        return notificationRedisTemplate.opsForHash().values(key).stream()
                .map(obj -> (Notification) obj)
                .collect(Collectors.toList());
    }

    public long countByReceiverIdAndReadFalse(Long receiverId) {
        String key = KEY_PREFIX + receiverId;
        return notificationRedisTemplate.opsForHash().values(key).stream()
                .map(obj -> (Notification) obj)
                .filter(n -> !n.isRead())
                .count();
    }
}
