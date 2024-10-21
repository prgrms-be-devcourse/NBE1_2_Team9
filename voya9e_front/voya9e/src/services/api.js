import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080';

export const login = async (credentials) => {
    const response = await axios.post(`${API_BASE_URL}/users/signin`, credentials, { withCredentials: true });
    return response.data;
};

export const signup = async (userData) => {
    const response = await axios.post(`${API_BASE_URL}/users/signup`, userData, { withCredentials: true });
    return response.data;
};

export const logout = async () => {
    const response = await axios.post(`${API_BASE_URL}/users/logout`, {}, { withCredentials: true });
    return response.data;
};

export const getUserInfo = async () => {
    const response = await axios.get(`${API_BASE_URL}/users/me`, { withCredentials: true });
    return response.data;
};

// 사용자 프로필 수정 요청
export const updateProfile = async (userId, userData) => {
    const response = await axios.put(`${API_BASE_URL}/users/${userId}`, userData, { withCredentials: true });
    return response.data;
};

// 비밀번호 변경 요청
export const changePassword = async (userId, changePasswordReq) => {
    const response = await axios.put(`${API_BASE_URL}/users/${userId}/password`, changePasswordReq, { withCredentials: true });
    return response.data;
};

// 회원 탈퇴 요청
export const deleteUser = async (userId) => {
    const response = await axios.delete(`${API_BASE_URL}/users/${userId}`, { withCredentials: true });
    return response.data;
};

export const fetchUserInfo = async () => {
    try {
        const response = await axios.get(`${API_BASE_URL}/users/me`, { withCredentials: true });
        console.log('서버 응답 (사용자 정보):', response.data);
        return response.data;
    } catch (error) {
        console.error('사용자 정보를 가져오는 데 실패했습니다:', error);
        return null;
    }
};

export const isAuthenticated = async () => {
    try {
        const response = await axios.get(`${API_BASE_URL}/users/me`, { withCredentials: true });
        return response.status === 200;
    } catch (error) {
        console.error('인증 실패:', error);
        return false;
    }
};

export const getGroups = async () => {
    const response = await axios.get(`${API_BASE_URL}/groups/user`, { withCredentials: true });
    return response.data;
};

export const createGroup = async (groupData) => {
    const response = await axios.post(`${API_BASE_URL}/groups`, groupData, { withCredentials: true });
    return response.data;
};

export const getGroupMembers = async (groupId) => {
    const response = await axios.get(`${API_BASE_URL}/groups/${groupId}/members`, { withCredentials: true });
    return response.data;
};

export const inviteMember = async (groupId, email) => {
    const response = await axios.post(`${API_BASE_URL}/groups/${groupId}/members`, null, { params: { email }, withCredentials: true });
    return response.data;
};

export const getGroupEvents = async (groupId) => {
    const response = await axios.get(`${API_BASE_URL}/events/group/${groupId}`, { withCredentials: true });
    return response.data;
};

export const getWeatherForecast = async (location) => {
    try {
        const response = await axios.get(`${API_BASE_URL}/forecast?city=${encodeURIComponent(location)}`);
        return response.data;
    } catch (error) {
        console.error('날씨 정보를 가져오는 데 실패했습니다:', error);
        throw error;
    }
};

export const fetchUnreadNotificationCount = async () => {
    try {
        const response = await axios.get('/notifications/unread-count', {
            withCredentials: true
        });
        return response.data;
    } catch (error) {
        console.error('읽지 않은 알림 개수 조회 실패:', error);
        return 0;
    }
};

export const fetchNotifications = async () => {
    try {
        const response = await axios.get('/notifications');
        return response.data;
    } catch (error) {
        console.error('알림 조회 실패:', error);
        throw error;
    }
};

export const acceptInvitation = async (invitationId) => {
    try {
        await axios.post(`/groups/invitations/${invitationId}/accept`);
    } catch (error) {
        console.error('초대 수락 실패:', error);
        throw error;
    }
};

export const rejectInvitation = async (invitationId) => {
    try {
        await axios.post(`/groups/invitations/${invitationId}/reject`);
    } catch (error) {
        console.error('초대 거절 실패:', error);
        throw error;
    }
};

export const markNotificationAsRead = async (notificationId) => {
    try {
        await axios.post(`/notifications/${notificationId}/read`);
    } catch (error) {
        console.error('알림 읽음 처리 실패:', error);
        throw error;
    }
};

export const markAllNotificationsAsRead = async () => {
    try {
        await axios.post('/notifications/mark-all-read', {}, { withCredentials: true });
        console.log('모든 알림이 읽음 처리되었습니다.');
    } catch (error) {
        console.error('모든 알림 읽음 처리 실패:', error);
        throw error;
    }
};

// 그룹 삭제 요청
export const deleteGroup = async (groupId) => {
    try {
        const response = await axios.delete(`${API_BASE_URL}/groups/${groupId}`, { withCredentials: true });
        return response.data;
    } catch (error) {
        console.error(`그룹 삭제 실패 (ID: ${groupId}):`, error);
        throw error;
    }
};

// 그룹 업데이트 요청
export const updateGroup = async (groupId, updatedData) => {
    try {
        const response = await axios.put(`${API_BASE_URL}/groups/${groupId}`, updatedData, { withCredentials: true });
        return response.data;
    } catch (error) {
        console.error(`그룹 업데이트 실패 (ID: ${groupId}):`, error);
        throw error;
    }
};

export const changeMemberRole = async (groupId, username, newRole) => {
    try {
        const response = await axios.put(`${API_BASE_URL}/groups/${groupId}/members/${username}/role`, null, {
            params: { role: newRole },  // 역할을 쿼리 파라미터로 전달
            withCredentials: true
        });
        return response.data;
    } catch (error) {
        console.error('역할 변경 실패:', error);
        throw error;
    }
};

export const removeMemberFromGroup = async (groupId, username) => {
    try {
        const response = await axios.delete(`${API_BASE_URL}/groups/${groupId}/members/${username}`, { withCredentials: true });
        return response.data;
    } catch (error) {
        console.error('멤버 삭제 실패:', error);
        throw error;
    }
};