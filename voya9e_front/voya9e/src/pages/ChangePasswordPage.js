import React, { useEffect, useState } from 'react';
import { fetchUserInfo, changePassword } from '../services/api';
import { useNavigate } from 'react-router-dom';

const ChangePasswordPage = () => {
  const [currentPassword, setCurrentPassword] = useState('');
  const [newPassword, setNewPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [userId, setUserId] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    const loadUserInfo = async () => {
      const user = await fetchUserInfo();
      setUserId(user.userId); // userId 설정
    };

    loadUserInfo();
  }, []);

  const handleChangePassword = async (e) => {
    e.preventDefault();
    if (newPassword !== confirmPassword) {
      alert('새 비밀번호가 일치하지 않습니다.');
      return;
    }
    try {
      await changePassword(userId, { currentPassword, newPassword });
      alert('비밀번호 변경 완료!');
      navigate("/mypage");
    } catch (error) {
      console.error('비밀번호 변경 오류:', error);
    }
  };

  return (
    <div className='userBody'>
    <div className="change-password-container">
      <h1>비밀번호 변경</h1>
      <form onSubmit={handleChangePassword}>
        <input className='userInput'
          type="password"
          placeholder="현재 비밀번호"
          value={currentPassword}
          onChange={(e) => setCurrentPassword(e.target.value)}
        />
        <input className='userInput'
          type="password"
          placeholder="새 비밀번호"
          value={newPassword}
          onChange={(e) => setNewPassword(e.target.value)}
        />
        <input className='userInput'
          type="password"
          placeholder="비밀번호 확인"
          value={confirmPassword}
          onChange={(e) => setConfirmPassword(e.target.value)}
        />
        <br/><br/>
        <button className='userButton' type="submit">비밀번호 변경하기</button>
      </form>
    </div>
    </div>
  );
};

export default ChangePasswordPage;