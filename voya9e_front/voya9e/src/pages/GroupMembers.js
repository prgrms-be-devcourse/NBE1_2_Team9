import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { getGroupMembers, getGroupEvents, changeMemberRole, removeMemberFromGroup } from '../services/api'; // API 함수 가져오기

const GroupMembers = () => {
    const { groupId } = useParams();
    const [members, setMembers] = useState([]);
    const [events, setEvents] = useState([]);
    const [loading, setLoading] = useState(true);
    const [actionLoading, setActionLoading] = useState(false); // 역할 변경, 삭제 등의 로딩 상태 추가
    const navigate = useNavigate();

    useEffect(() => {
        const fetchMembersAndEvents = async () => {
            try {
                setLoading(true); // 데이터 로딩 상태 설정
                const [memberResponse, eventResponse] = await Promise.all([
                    getGroupMembers(groupId),
                    getGroupEvents(groupId)
                ]);
                setMembers(memberResponse);
                setEvents(eventResponse);
            } catch (error) {
                console.error('데이터를 불러오는 중 오류 발생:', error);
                alert('데이터를 불러오는 중 문제가 발생했습니다.');
            } finally {
                setLoading(false); // 데이터 로딩 완료 후 상태 해제
            }
        };
        fetchMembersAndEvents();
    }, [groupId]);

    const getRoleName = (role) => {
        if (role === 'OWNER') {
            return '소유자';
        } else if (role === 'ADMIN') {
            return '관리자';
        } else {
            return '멤버';
        }
    };

    const handleAddMember = () => {
        navigate(`/invite-member/${groupId}`);
    };

    const handleAddEvent = () => {
        navigate(`/eventdetail/${groupId}`);
    };

    const handleAccountBook = () => {
        navigate(`/accountBook/${groupId}`);
    };

    const handleAddSchedule = (eventId) => {
        navigate(`/schedule/${eventId}`);
    };

    const handleChangeRole = async (username, newRole) => {
        try {
            setActionLoading(true); // 요청 중 로딩 상태 표시
            await changeMemberRole(groupId, username, newRole); // 역할 변경 API 호출
            setMembers((prevMembers) => prevMembers.map(
                member => member.username === username ? { ...member, role: newRole } : member
            ));
            alert(`${getRoleName(newRole)} 권한으로 변경되었습니다.`);
        } catch (error) {
            console.error('역할 변경 중 오류 발생:', error);
        } finally {
            setActionLoading(false); // 요청 완료 후 로딩 상태 해제
        }
    };

    const handleRemoveMember = async (username) => {
        try {
            if (!window.confirm('정말로 이 멤버를 삭제하시겠습니까?')) return; // 확인 팝업
            setActionLoading(true); // 삭제 요청 중 로딩 상태 표시
            await removeMemberFromGroup(groupId, username); // 멤버 삭제 API 호출
            setMembers((prevMembers) => prevMembers.filter(member => member.username !== username));
            alert('멤버가 삭제되었습니다.');
        } catch (error) {
            console.error('멤버 삭제 중 오류 발생:', error);
        } finally {
            setActionLoading(false); // 요청 완료 후 로딩 상태 해제
        }
    };

    if (loading) {
        return <div>로딩 중...</div>;
    }

    return (
        <div className='groupList'>
            <div className='groupMemberList'>
                <h2>그룹 멤버 목록</h2>
                <button className='groupBtn' onClick={handleAddMember} style={{ marginLeft: '10px' }} disabled={actionLoading}>멤버 추가하기</button>
            </div>
            <ul>
                {members.map(member => (
                    <li className='groupMemberLi' key={member.username}>
                        {member.username} ({getRoleName(member.role)})
                        <div className='buttonGroup'>
                            {member.role !== 'OWNER' && ( // 소유자는 역할 변경을 하지 않도록 예외 처리
                                <>
                                    <button className='userButton editBtn' onClick={() => handleChangeRole(member.username, 'ADMIN')} disabled={actionLoading}>관리자 권한 부여</button>
                                    <button className='userButton editBtn' onClick={() => handleChangeRole(member.username, 'MEMBER')} disabled={actionLoading}>멤버 권한 부여</button>
                                </>
                            )}
                            <button className='userButton deleteBtn' onClick={() => handleRemoveMember(member.username)} disabled={actionLoading}>삭제</button>
                        </div>
                    </li>
                ))}
            </ul>
            <div style={{ marginTop: '30px' }}>
                <h2>일정 목록</h2>
                <button className='groupBtn' onClick={handleAddEvent} style={{ marginLeft: '10px' }} disabled={actionLoading}>일정 추가하기</button>
                <ul>
                    {events.length > 0 ? (
                        events.map(event => (
                            <li className='groupMemberLi' key={event.id} onClick={() => handleAddSchedule(event.id)}>
                                {event.eventName} <br /> {event.city} <br /> {event.startDate + "~" + event.endDate}
                            </li>
                        ))
                    ) : (
                        <li className='groupMemberLi'>등록된 일정이 없습니다.</li>
                    )}
                </ul>
            </div>
            <button className='expenseBookBtn' onClick={handleAccountBook} style={{ marginLeft: '10px' }} disabled={actionLoading}>가계부</button>
        </div>
    );
};

export default GroupMembers;