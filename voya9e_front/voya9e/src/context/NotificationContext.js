import React, { createContext, useState, useEffect, useContext, useCallback } from 'react';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { getUserInfo, fetchNotifications, fetchUnreadNotificationCount } from '../services/api';

const NotificationContext = createContext();

export const NotificationProvider = ({ children }) => {
    const [notifications, setNotifications] = useState([]);
    const [unreadCount, setUnreadCount] = useState(0);
    const [userId, setUserId] = useState(null);

    const updateUnreadCount = useCallback(async () => {
        try {
            const count = await fetchUnreadNotificationCount();
            setUnreadCount(count);
        } catch (error) {
            console.error('읽지 않은 알림 개수 갱신 중 오류 발생: ', error);
        }
    }, []);

    useEffect(() => {
        let stompClient;

        const initializeNotifications = async () => {
            try {
                const userInfo = await getUserInfo();
                setUserId(userInfo.userId);

                const fetchedNotifications = await fetchNotifications();
                setNotifications(fetchedNotifications);

                await updateUnreadCount();

                const socketUrl = 'http://localhost:8080/ws';
                const socket = new SockJS(socketUrl);

                stompClient = new Client({
                    webSocketFactory: () => socket,
                    connectHeaders: {
                        userId: userInfo.userId.toString()
                    },
                    reconnectDelay: 5000,
                    onConnect: () => {
                        console.log('웹소켓 연결 성공');
                        stompClient.subscribe(`/topic/user/${userInfo.userId}`, (message) => {
                            const newNotification = JSON.parse(message.body);
                            setNotifications(prev => {
                                // 중복 알림 방지
                                if (!prev.some(n => n.id === newNotification.id)) {
                                    return [...prev, newNotification];
                                }
                                return prev;
                            });
                            updateUnreadCount();
                        });
                        // 새로운 구독 추가
                        stompClient.subscribe(`/topic/user/${userInfo.userId}/unreadCount`, (message) => {
                            const newUnreadCount = parseInt(message.body);
                            setUnreadCount(newUnreadCount);
                        });
                    },
                    onStompError: (frame) => {
                        console.error('STOMP 오류 발생', frame.headers['message']);
                    }
                });

                stompClient.activate();
            } catch (error) {
                console.error('알림 초기화 중 오류 발생:', error);
            }
        };

        initializeNotifications();

        return () => {
            if (stompClient && stompClient.active) stompClient.deactivate();
        };
    }, [updateUnreadCount]);

    const markAsRead = useCallback((notificationId) => {
        setNotifications(prev =>
            prev.map(n => n.id === notificationId ? { ...n, read: true } : n)
        );
        updateUnreadCount();
    }, [updateUnreadCount]);

    const markAllAsRead = useCallback(() => {
        setNotifications(prev => prev.map(n => ({ ...n, read: true })));
        setUnreadCount(0);
    }, []);

    return (
        <NotificationContext.Provider value={{ notifications, unreadCount, markAsRead, updateUnreadCount, userId, markAllAsRead }}>
            {children}
        </NotificationContext.Provider>
    );
};

export const useNotification = () => useContext(NotificationContext);