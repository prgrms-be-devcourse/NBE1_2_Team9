import React, { useEffect, useState } from 'react';
import { fetchUserInfo } from '../services/api'; // 사용자 정보 API 요청 함수
import './user.css';
import { useNavigate } from 'react-router-dom';

const MyPage = () => {
  const [user, setUser] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    const loadUserInfo = async () => {
      const userInfo = await fetchUserInfo();
      setUser(userInfo);
    };
    
    loadUserInfo();
  }, []);

  const handleUpdateProfile =()=>{
    navigate('/update-profile');
  }

  if (!user) {
    return <p>사용자 정보를 불러올 수 없습니다.</p>;
  }

  return (
    <div className='userBody'>
    <div className="mypage-container">
      <h1>마이페이지</h1>
      <p>사용자 이름: {user.username}</p>
      <p>이메일: {user.email}</p>
      <p>가입일: {user.joinedDate}</p>
      <p>역할: {user.role}</p>
      <button className='mypageBtn' onClick={handleUpdateProfile}>프로필 수정</button>
    </div>
    </div>
  );
};

export default MyPage;