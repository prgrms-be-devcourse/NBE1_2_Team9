import React, { createContext, useState, useEffect, useContext, useCallback, useRef } from 'react';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { getUserInfo, fetchNotifications, fetchUnreadNotificationCount } from '../services/api';

const NotificationContext = createContext();

export const NotificationProvider = ({ children }) => {
    const [notifications, setNotifications] = useState([]);
    const [unreadCount, setUnreadCount] = useState(0);
    const [userId, setUserId] = useState(null);
    const stompClientRef = useRef(null);  // stompClient 상태를 유지할 수 있는 ref 사용

    const updateUnreadCount = useCallback(async () => {
        try {
            const count = await fetchUnreadNotificationCount();
            setUnreadCount(count);
        } catch (error) {
            console.error('읽지 않은 알림 개수 갱신 중 오류 발생: ', error);
        }
    }, []);

    useEffect(() => {
        const initializeNotifications = async () => {
            try {
                const userInfo = await getUserInfo();
                setUserId(userInfo.userId);

                // 중복 연결 방지
                if (stompClientRef.current && stompClientRef.current.active) {
                    console.log('이미 웹소켓이 활성화되어 있습니다.');
                    return;
                }

                const fetchedNotifications = await fetchNotifications();
                setNotifications(fetchedNotifications);

                await updateUnreadCount();

                const socketUrl = 'http://localhost:8080/ws';
                const socket = new SockJS(socketUrl);

                // stompClient를 ref로 관리하여 컴포넌트 리렌더링 시 유지
                stompClientRef.current = new Client({
                    webSocketFactory: () => socket,
                    connectHeaders: {
                        userId: userInfo.userId.toString()
                    },
                    reconnectDelay: 5000,
                    onConnect: () => {
                        console.log('웹소켓 연결 성공');
                        stompClientRef.current.subscribe(`/topic/user/${userInfo.userId}`, (message) => {
                            const newNotification = JSON.parse(message.body);
                            setNotifications(prev => {
                                if (!prev.some(n => n.id === newNotification.id)) {
                                    return [...prev, newNotification];
                                }
                                return prev;
                            });
                            updateUnreadCount();
                        });
                        stompClientRef.current.subscribe(`/topic/user/${userInfo.userId}/unreadCount`, (message) => {
                            const newUnreadCount = parseInt(message.body);
                            setUnreadCount(newUnreadCount);
                        });
                    },
                    onStompError: (frame) => {
                        console.error('STOMP 오류 발생', frame.headers['message']);
                    }
                });

                stompClientRef.current.activate();
            } catch (error) {
                console.error('알림 초기화 중 오류 발생:', error);
            }
        };

        initializeNotifications();

        return () => {
            if (stompClientRef.current && stompClientRef.current.active) {
                stompClientRef.current.deactivate();
            }
        };
    }, [updateUnreadCount]); // `updateUnreadCount`만 의존성으로 설정, 웹소켓 연결 한 번만 실행

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