import React, { useEffect, useState } from 'react';
import { fetchUserInfo, getWeatherForecast } from '../services/api';
import WeatherSearch from '../components/WeatherSearch';
import styled from 'styled-components';
import { useNavigate } from 'react-router-dom';

const MainPage = () => {
  const [userInfo, setUserInfo] = useState(null);
  const [weatherData, setWeatherData] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    const loadUserInfo = async () => {
      const user = await fetchUserInfo();
      if (user) {
        setUserInfo(user);
      }
    };

    loadUserInfo();
  }, []);

  const handleChatBot=()=>{
    navigate('/chat');
  }
  const handleGroupView = () => {
    navigate('/groups');
  };

  const handleWeatherSearch = async (location) => {
    try {
      const data = await getWeatherForecast(location);
      setWeatherData(data);
    } catch (error) {
      console.error('날씨 정보를 가져오는 데 실패했습니다:', error);
    }
  };

  return (
    <div className='userBody'>
      <MainContainer>
        {userInfo ? (
          <>
            <WelcomeText>{`${userInfo.username}님 환영합니다!`}</WelcomeText>
            <WeatherSearch onSearch={handleWeatherSearch} />
            <TravelOptions>
              <OptionButton>찜한 여행지</OptionButton>
              <OptionButton onClick={handleGroupView}>그룹 보기</OptionButton>
              <OptionButton>찜한 장소</OptionButton>
              <OptionButton onClick={handleChatBot}>챗봇</OptionButton>
            </TravelOptions>
            {weatherData && (
            
              <WeatherContainer>
                <h3>현재 날씨</h3>
                <div>온도: {weatherData.list[0].main.temp}°C</div>
                <div>날씨: {weatherData.list[0].weather[0].description}</div>
              </WeatherContainer>
            )}
          </>
        ) : (
          <h2>로그인이 필요합니다.</h2>
        )}
      </MainContainer>
    </div>
  );
};

// MainPage 스타일링
const MainContainer = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 80vh;
  text-align: center;
`;

const WelcomeText = styled.h2`
  font-size: 2rem;
  color: #333;
  margin-bottom: 2rem;
`;

// const SearchInput = styled.input`
//   padding: 0.8rem;
//   width: 300px;
//   font-size: 1rem;
//   border: 2px solid #ddd;
//   border-radius: 5px;
//   margin-bottom: 2rem;

//   &:focus {
//     outline: none;
//     border-color: #008CFF;
//   }
// `;

const TravelOptions = styled.div`
  display: flex;
  gap: 1rem;
  flex-wrap: wrap;
  justify-content: center;
`;

const OptionButton = styled.button`
  padding: 0.8rem 1.5rem;
  font-size: 1rem;
  background-color: #008CFF;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  transition: background-color 0.3s ease;

  &:hover {
    background-color: #007ACC;  /* 좀 더 어두운 파랑색으로 hover 효과 */
  }
`;

const WeatherContainer = styled.div`
  margin: 20px 0;
  text-align: center;
`;

export default MainPage;