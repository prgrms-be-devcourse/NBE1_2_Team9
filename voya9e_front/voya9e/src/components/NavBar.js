import React, { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import styled from 'styled-components';
import { logout, isAuthenticated, fetchUnreadNotificationCount } from '../services/api';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

const NavBar = () => {
  const [loggedIn, setLoggedIn] = useState(false);
  const [notificationCount, setNotificationCount] = useState(0);
  const navigate = useNavigate();
  const userId = localStorage.getItem('userId');

  useEffect(() => {
    const checkAuth = async () => {
      const authenticated = await isAuthenticated();
      setLoggedIn(authenticated);
    };
    checkAuth();

    if (loggedIn) {
      // 초기 알림 개수 로드
      const loadNotificationCount = async () => {
        try {
          const count = await fetchUnreadNotificationCount();
          setNotificationCount(count);
        } catch (error) {
          console.error('알림 개수 로드 중 오류 발생:', error);
        }
      };
      loadNotificationCount();

      // 웹소켓 연결
      const socketUrl = 'http://localhost:8080/ws';
      const socket = new SockJS(socketUrl);

      const stompClient = new Client({
        webSocketFactory: () => socket,
        reconnectDelay: 5000,
        onConnect: () => {
          console.log('웹소켓 연결 성공');
          stompClient.subscribe(`/topic/user/${userId}`, (message) => {
            console.log('새 알림 수신:', message.body);
            setNotificationCount(prevCount => prevCount + 1);
          });
        },
        onStompError: (frame) => {
          console.error('STOMP 오류 발생', frame.headers['message']);
        }
      });

      stompClient.activate();

      return () => {
        if (stompClient) stompClient.deactivate();
      };
    }
  }, [loggedIn, userId]);

  const handleLogout = () => {
    logout();
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
                    알림 {notificationCount > 0 && `(${notificationCount})`}
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

const NavItem = styled.li``;

const StyledLink = styled(Link)`
  color: #fff;
  text-decoration: none;
  font-size: 1.2rem;
  font-weight: bold;
  transition: color 0.3s ease;

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

export default NavBar;