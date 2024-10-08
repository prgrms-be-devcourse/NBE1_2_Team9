import React, { useEffect, useState } from 'react';
import { fetchUserInfo, updateProfile } from '../services/api'; // 사용자 정보 및 업데이트 API 함수

const UpdateProfilePage = () => {
  const [userInfo, setUserInfo] = useState(null);
  const [username, setUsername] = useState('');
  const [email, setEmail] = useState('');

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
      await updateProfile({ username, email });
      alert('프로필 업데이트 완료!');
    } catch (error) {
      console.error('프로필 업데이트 오류:', error);
    }
  };

  if (!userInfo) {
    return <p>사용자 정보를 불러오는 중...</p>;
  }

  return (
    <div className="update-profile-container">
      <h1>프로필 수정</h1>
      <form onSubmit={handleUpdateProfile}>
        <input
          type="text"
          placeholder="사용자 이름"
          value={username}
          onChange={(e) => setUsername(e.target.value)}
        />
        <input
          type="email"
          placeholder="이메일"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
        />
        <br/><br/>
        <button type="submit">프로필 수정하기</button>
      </form>
    </div>
  );
};

export default UpdateProfilePage;