import React, { useEffect, useState } from 'react';
import { fetchUserInfo, getWeatherForecast } from '../services/api';
import styled from 'styled-components';
import { useNavigate } from 'react-router-dom';
import voya9eLogo from '../voya9eLogo.png';

const MainPage = () => {
    const [userInfo, setUserInfo] = useState(null);
    const [weatherData, setWeatherData] = useState(null);
    const [errorMessage, setErrorMessage] = useState('');
    const [searchLocation, setSearchLocation] = useState('');
    const [isWeatherVisible, setIsWeatherVisible] = useState(false); // 날씨 토글 상태
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

    const handleChatBot = () => {
        navigate('/chat');
    };
    const handleGroupView = () => {
        navigate('/groups');
    };

    const handleExchangeRate = () => {
        navigate('/exchange-rate');
    };

    // 날씨 검색 핸들러
    const handleWeatherSearch = async () => {
        if (!isWeatherVisible) {
            try {
                const data = await getWeatherForecast(searchLocation);
                if (data && data.list && data.list.length > 0) {
                    setWeatherData(data);
                    setErrorMessage('');
                } else {
                    setErrorMessage('해당 도시의 날씨 정보를 찾을 수 없습니다.');
                }
            } catch (error) {
                console.error('날씨 정보를 가져오는 데 실패했습니다:', error);
                setErrorMessage('날씨 정보를 가져오는 중 오류가 발생했습니다.');
            }
        }

        setIsWeatherVisible(!isWeatherVisible); // 날씨 정보 표시/숨김 토글
    };

    return (
        <div className='userBody'>
            <MainContainer>
                <img className='voya9eLogo' src={voya9eLogo} width='300px' />
                {userInfo ? (
                    <>
                        <WelcomeText>{`${userInfo.username}님 환영합니다!`}</WelcomeText>
                        <SearchInput
                            type="text"
                            placeholder="도시 이름을 입력하세요"
                            value={searchLocation}
                            onChange={(e) => setSearchLocation(e.target.value)}
                        />
                        <TravelOptions>
                            <OptionButton onClick={handleWeatherSearch}>
                                {isWeatherVisible ? '날씨 숨기기' : '날씨 검색'}
                            </OptionButton>
                            <OptionButton onClick={handleGroupView}>그룹 보기</OptionButton>
                            <OptionButton onClick={handleChatBot}>챗봇</OptionButton>
                            <OptionButton onClick={handleExchangeRate}>환율</OptionButton>
                        </TravelOptions>
                        {errorMessage && <ErrorMessage>{errorMessage}</ErrorMessage>}

                        {isWeatherVisible && weatherData && (
                            <WeatherContainer>
                                <WeatherGrid>
                                    {weatherData.list.slice(0, 8).map((weatherItem, index) => {
                                        const date = new Date(weatherItem.dt * 1000);
                                        const time = `${date.getHours()}:00`;
                                        const temp = weatherItem.main.temp.toFixed(1);
                                        const iconCode = weatherItem.weather[0].icon;
                                        const iconUrl = `http://openweathermap.org/img/wn/${iconCode}@2x.png`;

                                        return (
                                            <WeatherCard key={index}>
                                                <WeatherTime>{time}</WeatherTime>
                                                <WeatherIcon src={iconUrl} alt="날씨 아이콘" />
                                                <WeatherTemp>{temp}°C</WeatherTemp>
                                                <WeatherDescription>{weatherItem.weather[0].description}</WeatherDescription>
                                            </WeatherCard>
                                        );
                                    })}
                                </WeatherGrid>
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
    background-color: #007ACC;
  }
`;

const SearchInput = styled.input`
  padding: 0.8rem;
  width: 300px;
  font-size: 1rem;
  border: 2px solid #ddd;
  border-radius: 5px;
  margin-bottom: 2rem;

  &:focus {
    outline: none;
    border-color: #008CFF;
  }
`;

const WeatherContainer = styled.div`
  margin: 20px 0;
  text-align: center;
`;

const WeatherGrid = styled.div`
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
`;

const WeatherCard = styled.div`
  background-color: #f0f0f0;
  border-radius: 10px;
  padding: 10px;
  text-align: center;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
`;

const WeatherTime = styled.div`
  font-size: 1.2rem;
  margin-bottom: 10px;
`;

const WeatherIcon = styled.img`
  width: 50px;
  height: 50px;
  margin-bottom: 10px;
`;

const WeatherTemp = styled.div`
  font-size: 1.5rem;
  font-weight: bold;
`;

const WeatherDescription = styled.div`
  font-size: 1rem;
  color: #555;
`;

const ErrorMessage = styled.div`
  color: red;
  margin-top: 1rem;
`;

export default MainPage;
