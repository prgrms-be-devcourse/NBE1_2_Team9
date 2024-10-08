import React from 'react';
import { deleteUser, logout } from '../services/api';  // API에서 로그아웃과 계정 삭제 기능을 가져옴
import { useNavigate } from 'react-router-dom';

const DeleteAccountPage = () => {
  const navigate = useNavigate();

  const handleDeleteAccount = async () => {
    try {
      await deleteUser();  // 계정 삭제 요청
      alert('계정이 삭제되었습니다.');
      await logout();      // 로그아웃 처리
      navigate('/signup'); // 회원가입 페이지로 리디렉션
    } catch (error) {
      console.error('계정 삭제 오류:', error);
    }
  };

  return (
    <div className="delete-account-container">
      <h1>계정 삭제</h1>
      <p>계정을 삭제하시겠습니까? 이 작업은 되돌릴 수 없습니다.</p>
      <button onClick={handleDeleteAccount}>계정 삭제</button>
    </div>
  );
};

export default DeleteAccountPage;