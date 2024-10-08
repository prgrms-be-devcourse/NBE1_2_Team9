import React from 'react';
import { Route, Routes } from 'react-router-dom';
import LoginPage from './pages/LoginPage';
import SignupPage from './pages/SignupPage';
import MainPage from './pages/MainPage';
import MyPage from './pages/MyPage';
import UpdateProfilePage from './pages/UpdateProfilePage';
import ChangePasswordPage from './pages/ChangePasswordPage';
import DeleteAccountPage from './pages/DeleteAccountPage';
import NavBar from './components/NavBar';

const App = () => {
  return (
    <>
      <NavBar />
      
      <Routes>
        <Route path="/" element={<MainPage />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/signup" element={<SignupPage />} />
        <Route path="/mypage" element={<MyPage />} />
        <Route path="/update-profile" element={<UpdateProfilePage />} />
        <Route path="/change-password" element={<ChangePasswordPage />} />
        <Route path="/delete-account" element={<DeleteAccountPage />} />
      </Routes>
    </>
  );
};

export default App;