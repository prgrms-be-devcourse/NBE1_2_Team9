import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { getGroupMembers, getGroupEvents } from '../services/api';

const GroupMembers = () => {
    const { groupId } = useParams();
    const [members, setMembers] = useState([]);
    const [events, setEvents] = useState([]);
    const [loading, setLoading] = useState(true);
    const navigate = useNavigate();

    useEffect(() => {
        const fetchMembersAndEvents = async () => {
            try {
                const [memberResponse, eventResponse] = await Promise.all([
                    getGroupMembers(groupId),
                    getGroupEvents(groupId)
                ]);
                setMembers(memberResponse);
                setEvents(eventResponse);
                setLoading(false);
            } catch (error) {
                console.error('데이터를 불러오는 중 오류 발생:', error);
                setLoading(false);
            }
        };
        fetchMembersAndEvents();
    }, [groupId]);

    const getRoleName = (role) => {
        return role === 'ADMIN' ? '관리자' : '멤버';
    };

    const handleAddMember = () => {
        navigate(`/invite-member/${groupId}`);
    };

    const handleAddEvent = () => {
        navigate(`/add-event/${groupId}`);
    };

    if (loading) {
        return <div>로딩 중...</div>;
    }

    return (
        <div>
            <h2>그룹 멤버 목록</h2>
            <button onClick={handleAddMember} style={{marginLeft: '10px'}}>멤버 추가하기</button>
            <ul>
                {members.map(member => (
                    <li key={member.id}>
                        {member.username} ({getRoleName(member.role)})
                    </li>
                ))}
            </ul>

            <div style={{marginTop: '30px'}}>
                <h2>일정 목록</h2>
                <button onClick={handleAddEvent} style={{marginLeft: '10px'}}>일정 추가하기</button>
                <ul>
                    {events.length > 0 ? (
                        events.map(event => (
                            <li key={event.eventId}>
                                {event.title} - {new Date(event.date).toLocaleDateString()}
                            </li>
                        ))
                    ) : (
                        <li>등록된 일정이 없습니다.</li>
                    )}
                </ul>
            </div>
        </div>
    );
};

export default GroupMembers;