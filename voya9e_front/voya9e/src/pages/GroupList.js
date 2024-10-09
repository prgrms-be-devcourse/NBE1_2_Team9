import React, { useState, useEffect } from 'react';
import { getGroups } from '../services/api';
import { useNavigate } from 'react-router-dom';
import './Group.css';

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
        <div className='groupList'>
            <div className='groupListTitle'>
                <h2 className='title'>내 여행 그룹</h2>
                <button className='groupBtn' onClick={() => navigate('/create-group')}>그룹 추가하기</button>
            </div>
            <hr/>
            <ul>
                {groups.map(group => (
                    <li className='group' key={group.groupId} onClick={() => handleGroupClick(group.groupId)} style={{ cursor: 'pointer' }}>
                        {group.groupName}
                    </li>
                ))}
            </ul>
        </div>
    );
};

export default GroupList;