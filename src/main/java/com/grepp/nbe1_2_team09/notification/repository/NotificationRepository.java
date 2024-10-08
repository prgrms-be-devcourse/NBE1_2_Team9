package com.grepp.nbe1_2_team09.notification.repository;

import com.grepp.nbe1_2_team09.notification.entity.Notification;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
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

    //모두 읽기처리 배치 처리 최적화
    public void markAllAsReadBulk(Long receiverId){
        String key = KEY_PREFIX + receiverId;
        Map<Object, Object> not = notificationRedisTemplate.opsForHash().entries(key); //key에 해당하는 value(알림)들을 모두 가져옴

        Map<Object, Object> updatedNotification = not.entrySet().stream()
                .map(e -> {
                    Notification notification = (Notification) e.getValue(); // 알림 객체 가져옴
                    if(!notification.isRead()){
                        notification.setRead(true); // 안읽었으면 읽음처리
                    }
                    return Map.entry(e.getKey(), notification); // 새로운 map으로 키랑 value(알림)을 return
                })
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)); // 처리된 알림들 collect -> map
        notificationRedisTemplate.opsForHash().putAll(key, updatedNotification); // 업데이트 다시 Redis로
    }
}
