import React from 'react';
import { acceptInvitation, rejectInvitation, markNotificationAsRead } from '../services/api';
import { useNotification } from '../context/NotificationContext';

const Notification = () => {
    const { notifications, markAsRead } = useNotification();

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
        <div>
            <h2>알림</h2>
            <ul>
                {notifications.map((notification) => (
                    <li key={notification.id}>
                        {notification.message}
                        {notification.type === 'INVITE' && !notification.read && (
                            <>
                                <button onClick={() => handleAccept(notification)}>수락</button>
                                <button onClick={() => handleReject(notification)}>거절</button>
                            </>
                        )}
                        {notification.type === 'ACCEPT' && (
                            <span>초대 수락 완료</span>
                        )}
                        {notification.type === 'REJECT' && (
                            <span>초대 거절 완료</span>
                        )}
                    </li>
                ))}
            </ul>
        </div>
    );
};

export default Notification;