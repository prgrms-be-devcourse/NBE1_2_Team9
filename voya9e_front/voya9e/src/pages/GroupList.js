import React, { useState, useEffect } from 'react';
import { getGroups, deleteGroup, updateGroup } from '../services/api';
import { useNavigate } from 'react-router-dom';
import './Group.css';

const GroupList = () => {
    const [groups, setGroups] = useState([]);
    const [loading, setLoading] = useState(true);
    const [editingGroupId, setEditingGroupId] = useState(null);
    const [newGroupName, setNewGroupName] = useState('');
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

    const handleDeleteGroup = async (groupId) => {
        const confirmDelete = window.confirm('정말 이 그룹을 삭제하시겠습니까?');
        if (!confirmDelete) return;

        try {
            await deleteGroup(groupId);
            setGroups(groups.filter(group => group.groupId !== groupId));
            alert('그룹이 성공적으로 삭제되었습니다.');
        } catch (error) {
            console.error('그룹 삭제 중 오류 발생:', error);
            alert('그룹 삭제에 실패했습니다. 다시 시도해주세요.');
        }
    };

    const handleEditGroup = (groupId, currentName) => {
        setEditingGroupId(groupId);
        setNewGroupName(currentName);
    };

    const handleCancelEdit = () => {
        setEditingGroupId(null);
        setNewGroupName('');
    };

    const handleSaveGroupName = async (groupId) => {
        if (newGroupName.trim().length < 2 || newGroupName.trim().length > 50) {
            alert('그룹 이름은 2글자 이상 50글자 미만으로 입력해주세요.');
            return;
        }

        try {
            const updatedGroup = await updateGroup(groupId, { groupName: newGroupName.trim() });
            setGroups(groups.map(group => group.groupId === groupId ? updatedGroup : group));
            setEditingGroupId(null);
            setNewGroupName('');
            alert('그룹 이름이 성공적으로 변경되었습니다.');
        } catch (error) {
            console.error('그룹 이름 변경 중 오류 발생:', error);
            alert('그룹 이름 변경에 실패했습니다. 다시 시도해주세요.');
        }
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
            <ul className='groupListUl'>
                {groups.map(group => (
                    <li className='group' key={group.groupId}>
                        {editingGroupId === group.groupId ? (
                            <div className='editMode'>
                                <input
                                    type="text"
                                    value={newGroupName}
                                    onChange={(e) => setNewGroupName(e.target.value)}
                                    className='userInput editInput'
                                />
                                <div className='buttonGroup'>
                                    <button
                                        className='userButton saveBtn'
                                        onClick={() => handleSaveGroupName(group.groupId)}
                                    >
                                        저장
                                    </button>
                                    <button
                                        className='userButton cancelBtn'
                                        onClick={handleCancelEdit}
                                    >
                                        취소
                                    </button>
                                </div>
                            </div>
                        ) : (
                            <div className='viewMode'>
                                <span
                                    onClick={() => handleGroupClick(group.groupId)}
                                    className='groupName'
                                >
                                    {group.groupName}
                                </span>
                                <div className='buttonGroup'>
                                    <button
                                        className='userButton editBtn'
                                        onClick={() => handleEditGroup(group.groupId, group.groupName)}
                                    >
                                        수정
                                    </button>
                                    <button
                                        className='userButton deleteBtn'
                                        onClick={() => handleDeleteGroup(group.groupId)}
                                    >
                                        삭제
                                    </button>
                                </div>
                            </div>
                        )}
                    </li>
                ))}
            </ul>
        </div>
    );
};

export default GroupList;