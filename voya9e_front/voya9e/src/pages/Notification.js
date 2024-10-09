import React, { useState, useEffect } from 'react';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { fetchNotifications, acceptInvitation, rejectInvitation, markNotificationAsRead } from '../services/api';

const Notification = () => {
    const [notifications, setNotifications] = useState([]);
    const userId = localStorage.getItem('userId');

    useEffect(() => {
        const loadInitialNotifications = async () => {
            try {
                const fetchedNotifications = await fetchNotifications();
                setNotifications(fetchedNotifications);
            } catch (error) {
                console.error('초기 알림 로드 중 오류 발생:', error);
            }
        };

        loadInitialNotifications();

        const socketUrl = 'http://localhost:8080/ws';
        const socket = new SockJS(socketUrl);

        const stompClient = new Client({
            webSocketFactory: () => socket,
            reconnectDelay: 5000,
            onConnect: () => {
                console.log('웹소켓 연결 성공');
                stompClient.subscribe(`/topic/user/${userId}`, (message) => {
                    console.log('새 알림 수신:', message.body);
                    const notification = JSON.parse(message.body);
                    setNotifications(prevNotifications => [...prevNotifications, notification]);
                });
            },
            onStompError: (frame) => {
                console.error('STOMP 오류 발생', frame.headers['message']);
            }
        });

        stompClient.activate();

        return () => {
            if (stompClient) stompClient.deactivate();
        };
    }, [userId]);

    const handleAccept = async (notification) => {
        if (!notification.invitationId) {
            console.error('초대 ID가 없습니다.');
            return;
        }
        try {
            await acceptInvitation(notification.invitationId);
            await markNotificationAsRead(notification.id);
            alert('초대를 수락했습니다.');
            removeNotification(notification.id);
        } catch (err) {
            console.error('수락 중 오류 발생:', err);
        }
    };

    const handleReject = async (notification) => {
        if (!notification.invitationId) {
            console.error('초대 ID가 없습니다.');
            return;
        }
        try {
            await rejectInvitation(notification.invitationId);
            await markNotificationAsRead(notification.id);
            alert('초대를 거절했습니다.');
            removeNotification(notification.id);
        } catch (err) {
            console.error('거절 중 오류 발생:', err);
        }
    };

    const removeNotification = (notificationId) => {
        setNotifications((prevNotifications) =>
            prevNotifications.filter(notification => notification.id !== notificationId)
        );
    };

    return (
        <div>
            <h2>알림</h2>
            <ul>
                {notifications.map((notification, index) => (
                    <li key={index}>
                        {notification.message}
                        {notification.type === 'INVITE' && (
                            <>
                                <button onClick={() => handleAccept(notification)}>수락</button>
                                <button onClick={() => handleReject(notification)}>거절</button>
                            </>
                        )}
                    </li>
                ))}
            </ul>
        </div>
    );
};

export default Notification;