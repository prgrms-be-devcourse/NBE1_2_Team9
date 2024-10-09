import React, { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import styled from 'styled-components';
import { logout, isAuthenticated } from '../services/api';
import { useNotification } from '../context/NotificationContext';

const NavBar = () => {
  const [loggedIn, setLoggedIn] = useState(false);
  const navigate = useNavigate();
  const { unreadCount } = useNotification();

  useEffect(() => {
    const checkAuth = async () => {
      const authenticated = await isAuthenticated();
      setLoggedIn(authenticated);
    };
    checkAuth();
  }, []);

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
                    알림 {unreadCount > 0 && `(${unreadCount})`}
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