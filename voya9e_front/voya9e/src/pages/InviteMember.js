import React, { useState } from 'react';
import { useParams } from 'react-router-dom';
import { inviteMember } from '../services/api';
import { useNotification } from '../context/NotificationContext';

const InviteMember = () => {
    const [email, setEmail] = useState('');
    const [message, setMessage] = useState('');
    const { groupId } = useParams();
    const { userId } = useNotification();

    const handleSubmit = async (e) => {
        e.preventDefault();

        if (!userId) {
            setMessage('사용자 정보를 가져올 수 없습니다. 다시 로그인해주세요.');
            return;
        }

        try {
            await inviteMember(groupId, email);
            setMessage('초대장이 발송되었습니다.');
            setEmail('');
        } catch (error) {
            console.error('멤버 초대 중 오류 발생:', error);
            setMessage('멤버 초대에 실패하였습니다. 다시 시도해 주세요.');
        }
    };

    return (
        <div className='CreateGroup'>
            <div className='CreateGrouppp'>
            <h2>멤버 초대</h2>
            <form className='createGroupForm' onSubmit={handleSubmit}>
                <div>
                    <label className='createGroupTitle'>이메일 </label>
                    <input
                        type="email"
                        value={email}
                        onChange={e => setEmail(e.target.value)}
                        required
                    />
                </div>
                {message && <div>{message}</div>}
                <button  className='createGroupBtn' type="submit">초대하기</button>
            </form>
            </div>
        </div>
    );
};

export default InviteMember;