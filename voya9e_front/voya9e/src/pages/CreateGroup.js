import React, { useState } from 'react';
import { createGroup } from '../services/api';
import { useNavigate } from 'react-router-dom';

const CreateGroup = () => {
    const [groupName, setGroupName] = useState('');
    const [error, setError] = useState('');
    const navigate = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            await createGroup({ groupName: groupName });
            navigate('/groups');
        } catch (error) {
            console.error('그룹 생성 중 오류 발생:', error);
            setError('그룹 생성에 실패하였습니다.');
        }
    };

    return (
        <div className='CreateGroup'>
            <form className='createGroupForm' onSubmit={handleSubmit}>
                <div>
                    <label className='createGroupTitle'>그룹 이름</label>
                    <input type="text" value={groupName} onChange={e => setGroupName(e.target.value)} required />
                </div>
                {error && <div style={{ color: 'red' }}>{error}</div>}
                <button className='createGroupBtn' type="submit">그룹 생성</button>
            </form>
        </div>
    );
};

export default CreateGroup;