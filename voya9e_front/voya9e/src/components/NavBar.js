import React, { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import styled from 'styled-components';
import {logout, isAuthenticated, fetchUnreadNotificationCount} from '../services/api';
import { useNotification } from '../context/NotificationContext';

const NavBar = ({ loggedIn, setLoggedIn }) => {
  const navigate = useNavigate();
  const { unreadCount, updateUnreadCount } = useNotification();

  useEffect(() => {
    const fetchUnreadCount = async () => {
      const count = await fetchUnreadNotificationCount();
      updateUnreadCount(count);
    };
    
    if (loggedIn) {
      fetchUnreadCount();
    }
  }, [loggedIn, updateUnreadCount]);

  const handleLogout = async () => {
    await logout();
    setLoggedIn(false);
    navigate('/login');
  };

  return (
    <Nav>
      <NavList>
        <NavItem>
          <StyledLink to="/">Home</StyledLink>
        </NavItem>

        {loggedIn ? (
          <>
            <NavItem>
              <StyledLink to="/mypage">MyPage</StyledLink>
            </NavItem>
            <NavItem>
              <LogoutButton onClick={handleLogout}>Logout</LogoutButton>
            </NavItem>
            <NavItem>
              <StyledLink to="/notifications">
                알림
                {unreadCount > 0 && <NotificationBadge>{unreadCount > 99 ? '99+' : unreadCount}</NotificationBadge>}
              </StyledLink>
            </NavItem>
          </>
        ) : (
          <>
            <NavItem>
              <StyledLink to="/login">Login</StyledLink>
            </NavItem>
            <NavItem>
              <StyledLink to="/signup">Signup</StyledLink>
            </NavItem>
          </>
        )}
      </NavList>
    </Nav>
  );
};

// 네비게이션 바 스타일링
const Nav = styled.nav`
  background-color: #008CFF;
  padding: 1rem 2rem;
  position: sticky;
  top: 0;
  width: 100%;
  z-index: 1000;
`;

const NavList = styled.ul`
  display: flex;
  justify-content: space-around;
  list-style: none;
  margin: 0;
  padding: 0;
`;

const NavItem = styled.li`
  position: relative; /* 배지 위치를 설정하기 위해 relative 추가 */
`;

const StyledLink = styled(Link)`
  color: #fff;
  text-decoration: none;
  font-size: 1.2rem;
  font-weight: bold;
  transition: color 0.3s ease;
  position: relative;

  &:hover {
    color: #E0E0E0;
  }
`;

const LogoutButton = styled.a`
  background: none;
  border: none;
  color: #fff;
  font-size: 1.2rem;
  font-weight: bold;
  cursor: pointer;
  transition: color 0.3s ease;

  &:hover {
    color: #E0E0E0;
  }
`;

const NotificationBadge = styled.span`
  background-color: red;
  color: white;
  font-size: 0.75rem;
  padding: 0.2rem 0.4rem;
  border-radius: 50%;
  position: absolute;
  top: -0.5rem;
  right: -1rem;
  display: flex;
  justify-content: center;
  align-items: center;
  min-width: 20px;
  height: 20px;
`;

export default NavBar;

