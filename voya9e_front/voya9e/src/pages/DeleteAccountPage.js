import React, { useEffect, useState } from 'react';
import { fetchUserInfo, deleteUser, logout } from '../services/api';
import { useNavigate } from 'react-router-dom';

const DeleteAccountPage = () => {
  const [userId, setUserId] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    const loadUserInfo = async () => {
      const user = await fetchUserInfo();
      setUserId(user?.userId);
    };

    loadUserInfo();
  }, []);

  const handleDeleteAccount = async () => {
    if (!window.confirm('정말로 계정을 삭제하시겠습니까? 이 작업은 되돌릴 수 없습니다.')) {
      return;
    }

    try {
      await deleteUser(userId);
      alert('계정이 삭제되었습니다.');
      await logout();
      
      navigate('/signup');
    } catch (error) {
      console.error('계정 삭제 오류:', error);
    }
  };

  if (!userId) {
    return <p>사용자 정보를 불러오는 중...</p>;
  }

  return (
    <div className='userBody'>
    <div className="delete-account-container">
      <h1>계정 삭제</h1>
      <p>계정을 삭제하시겠습니까? 이 작업은 되돌릴 수 없습니다.</p>
      <button className='userButton' onClick={handleDeleteAccount}>계정 삭제</button>
    </div>
    </div>
  );
};

export default DeleteAccountPage;