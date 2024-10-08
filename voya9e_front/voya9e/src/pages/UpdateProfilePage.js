import React, { useEffect, useState } from 'react';
import { fetchUserInfo, updateProfile } from '../services/api';
import { useNavigate } from 'react-router-dom';

const UpdateProfilePage = () => {
  const [userInfo, setUserInfo] = useState(null);
  const [username, setUsername] = useState('');
  const [email, setEmail] = useState('');
  const navigate = useNavigate();

  useEffect(() => {
    const loadUserInfo = async () => {
      const user = await fetchUserInfo();
      setUserInfo(user);
      setUsername(user?.username || '');
      setEmail(user?.email || '');
    };

    loadUserInfo();
  }, []);

  const handleUpdateProfile = async (e) => {
    e.preventDefault();
    try {
      const userId = userInfo.userId; // userInfo에서 userId 추출

      // userId와 함께 업데이트 데이터 전달
      await updateProfile(userId, { username, email });
      alert('프로필 업데이트 완료!');
      navigate('/mypage');
    } catch (error) {
      console.error('프로필 업데이트 오류:', error);
      alert('프로필 업데이트 실패');
    }
  };

  if (!userInfo) {
    return <p>사용자 정보를 불러오는 중...</p>;
  }

  return (
    <div className='userBody'>
      <div className="update-profile-container">
        <h1>프로필 수정</h1>
        <form onSubmit={handleUpdateProfile}>
          <input className='userInput'
            type="text"
            placeholder="사용자 이름"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
          />
          <input className='userInput'
            type="email"
            placeholder="이메일"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
          />
          <br/><br/>
          <button className='userButton' type="submit">프로필 수정하기</button>
        </form>
      </div>
    </div>
  );
};

export default UpdateProfilePage;