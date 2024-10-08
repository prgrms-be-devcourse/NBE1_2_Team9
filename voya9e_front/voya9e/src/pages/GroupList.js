import React, { useState, useEffect } from 'react';
import { getGroups } from '../services/api';
import { useNavigate } from 'react-router-dom';

const GroupList = () => {
    const [groups, setGroups] = useState([]);
    const [loading, setLoading] = useState(true);
    const navigate = useNavigate();

    useEffect(() => {
        const fetchGroups = async () => {
            try {
                console.log('그룹 데이터를 가져옵니다.');
                const response = await getGroups();
                console.log('API 응답:', response);
                setGroups(response);
                setLoading(false);
            } catch (error) {
                console.error('그룹 데이터를 불러오는 중 오류 발생:', error);
                setLoading(false);
            }
        };
        fetchGroups();
    }, []);

    const handleGroupClick = (groupId) => {
        navigate(`/group/${groupId}/members`);
    };

    if (loading) {
        return <div>로딩 중...</div>;
    }

    return (
        <div>
            <h2>내 여행 그룹</h2>
            <button onClick={() => navigate('/create-group')}>그룹 추가하기</button>
            <ul>
                {groups.map(group => (
                    <li key={group.groupId} onClick={() => handleGroupClick(group.groupId)} style={{ cursor: 'pointer' }}>
                        {group.groupName}
                    </li>
                ))}
            </ul>
        </div>
    );
};

export default GroupList;