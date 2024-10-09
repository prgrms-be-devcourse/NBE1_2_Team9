import React, {useEffect} from 'react';
import {acceptInvitation, rejectInvitation, markNotificationAsRead, markAllNotificationsAsRead} from '../services/api';
import { useNotification } from '../context/NotificationContext';
import './Group.css';

const Notification = () => {
    const { notifications, markAsRead } = useNotification();

    useEffect(() => {
        const markAllAsRead = async () => {
            try {
                await markAllNotificationsAsRead();
            } catch (err) {
                console.error('모든 알림 읽음 처리중 오류 발생:', err);
            }
        };
        markAllAsRead();
    }, []);

    const handleAccept = async (notification) => {
        if (!notification.invitationId) {
            console.error('초대 ID가 없습니다.');
            return;
        }
        try {
            await acceptInvitation(notification.invitationId);
            await markNotificationAsRead(notification.id);
            markAsRead(notification.id);
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
            markAsRead(notification.id);
        } catch (err) {
            console.error('거절 중 오류 발생:', err);
        }
    };

    return (
        <div className='notification-container'>
            <h2>알림</h2>
            <div className='notification'>
                <ul>
                    {notifications
                        .slice()
                        .sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt)) // 최신순 정렬
                        .map((notification) => (
                            <li key={notification.id} className="notification-item">
                                <p className="notification-message">{notification.message}</p>
                                {notification.type === 'INVITE' && !notification.read && (
                                    <div className="button-group">
                                        <button className="accept-btn" onClick={() => handleAccept(notification)}>수락</button>
                                        <button className="reject-btn" onClick={() => handleReject(notification)}>거절</button>
                                    </div>
                                )}
                                {notification.type === 'ACCEPT' && (
                                    <span className="status-text">초대 수락 완료</span>
                                )}
                                {notification.type === 'REJECT' && (
                                    <span className="status-text">초대 거절 완료</span>
                                )}
                            </li>
                        ))}
                </ul>
            </div>
        </div>
    );
};

export default Notification;