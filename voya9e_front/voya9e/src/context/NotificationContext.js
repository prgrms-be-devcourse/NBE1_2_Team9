import React, { createContext, useState, useEffect, useContext } from 'react';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { getUserInfo, fetchNotifications, fetchUnreadNotificationCount } from '../services/api';

const NotificationContext = createContext();

export const NotificationProvider = ({ children }) => {
    const [notifications, setNotifications] = useState([]);
    const [unreadCount, setUnreadCount] = useState(0);
    const [userId, setUserId] = useState(null);

    useEffect(() => {
        const initializeNotifications = async () => {
            try {
                const userInfo = await getUserInfo();
                setUserId(userInfo.userId);

                const fetchedNotifications = await fetchNotifications();
                setNotifications(fetchedNotifications);

                const count = await fetchUnreadNotificationCount();
                setUnreadCount(count);

                const socketUrl = 'http://localhost:8080/ws';
                const socket = new SockJS(socketUrl);

                const stompClient = new Client({
                    webSocketFactory: () => socket,
                    connectHeaders: {
                        userId: userInfo.userId.toString()
                    },
                    reconnectDelay: 5000,
                    onConnect: () => {
                        console.log('웹소켓 연결 성공');
                        stompClient.subscribe(`/topic/user/${userInfo.userId}`, (message) => {
                            const newNotification = JSON.parse(message.body);
                            setNotifications(prev => [...prev, newNotification]);
                            setUnreadCount(prev => prev + 1);
                        });
                    },
                    onStompError: (frame) => {
                        console.error('STOMP 오류 발생', frame.headers['message']);
                    }
                });

                stompClient.activate();

                return () => {
                    if (stompClient.active) stompClient.deactivate();
                };
            } catch (error) {
                console.error('알림 초기화 중 오류 발생:', error);
            }
        };

        initializeNotifications();
    }, []);

    const markAsRead = (notificationId) => {
        setNotifications(prev =>
            prev.map(n => n.id === notificationId ? { ...n, read: true } : n)
        );
        setUnreadCount(prev => Math.max(0, prev - 1));
    };

    return (
        <NotificationContext.Provider value={{ notifications, unreadCount, markAsRead, userId }}>
            {children}
        </NotificationContext.Provider>
    );
};

export const useNotification = () => useContext(NotificationContext);