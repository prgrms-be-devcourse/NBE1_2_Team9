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
export const updateProfile = async (userData) => {
    const response = await axios.put(`${API_BASE_URL}/users/me`, userData, { withCredentials: true });
    return response.data;
};

// 비밀번호 변경 요청
export const changePassword = async ({ currentPassword, newPassword }) => {
    const response = await axios.put(`${API_BASE_URL}/users/me/password`, { currentPassword, newPassword }, { withCredentials: true });
    return response.data;
};

// 회원 탈퇴 요청
export const deleteUser = async () => {
    const response = await axios.delete(`${API_BASE_URL}/users/me`, { withCredentials: true });
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