import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { login } from '../services/api';

const LoginPage = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const navigate = useNavigate();

  const handleLogin = async (e) => {
    e.preventDefault();
    try {
      // 로그인 API 호출
      await login({ email, password });
      
      navigate('/');
      window.location.reload();
    } catch (error) {
      console.error('로그인 오류:', error);
    }
  };

  const handleKakaoLogin = () => {
    window.location.href = '/users/signin/kakao';
  };

  return (
    <div className="login-container">
      <h1>voya9e</h1>
      <form onSubmit={handleLogin}>
        <input
          type="email"
          placeholder="이메일"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
        />
        <input
          type="password"
          placeholder="비밀번호"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
        />
        <br/>
        <br/>
        <button type="submit">로그인</button>
      </form>
      <br/>
      <button onClick={handleKakaoLogin} className="kakao-btn">
        <img src="/kakaotalk.png" alt="Kakao icon" width="16" /> 카카오 로그인
      </button>
      <a href="/users/signup">회원가입</a>
    </div>
  );
};

export default LoginPage;