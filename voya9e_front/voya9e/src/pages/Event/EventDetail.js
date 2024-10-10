import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import './EventDetail.css';

const EventDetail = () => {
    const navigate = useNavigate();
    const [eventName, setEventName] = useState('');
    const [description, setDescription] = useState('');
    const [city, setCity] = useState('');
    const [startDate, setStartDate] = useState('');
    const [endDate, setEndDate] = useState('');
    const { groupId } = useParams(); // URL에서 groupId 가져오기

    useEffect(() => {
        // sessionStorage에서 도시 정보와 날짜 정보 가져오기
        const storedCityData = sessionStorage.getItem('cityData');
        const storedDateData = sessionStorage.getItem('dateData');

        if (storedCityData) {
            setCity(JSON.parse(storedCityData).selectedCity);
        }

        if (storedDateData) {
            const { visitStart, visitEnd } = JSON.parse(storedDateData);
            setStartDate(visitStart);
            setEndDate(visitEnd);
        }
    }, []);

    const handleSubmit = async (e) => {
        e.preventDefault();

        // 유효성 검사
        if (eventName.length < 2 || eventName.length > 50) {
            alert('일정 이름은 2글자 이상 50글자 미만으로 해야합니다');
            return;
        }
        if (!city) {
            alert('여행 도시 선택 필수');
            return;
        }
        if (!startDate) {
            alert('시작 날짜 선택 필수');
            return;
        }
        if (!endDate) {
            alert('종료 날짜 선택 필수');
            return;
        }
        

        // API 호출
        const response = await fetch('/events', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                eventName,
                description,
                city,
                startDate,
                endDate,
                groupId,
            }),
        });

        if (response.ok) {
            alert('일정이 성공적으로 추가되었습니다!');
            sessionStorage.removeItem('cityData'); // sessionStorage 초기화
            sessionStorage.removeItem('dateData'); // sessionStorage 초기화

            // 그룹 멤버 페이지로 이동
            navigate(`/group/${groupId}/members`);
        } else {
            alert('일정 추가에 실패했습니다.');
        }
    };

    return (
        <div className="place-detail">
            <div className="header">
                <h1>일정 추가하기</h1>
            </div>
            <form onSubmit={handleSubmit} className="detail-content">
                <div className="info-buttons">
                    <button type="button" className="learn-more" onClick={() => navigate('/citysearch', { state: { groupId } })}>
                        도시 검색
                    </button>
                    <button type="button" className="learn-more" onClick={() => navigate('/calendar', { state: { groupId } })}>
                        날짜 검색
                    </button>
                    <input
                    type="text"
                    placeholder="일정 이름"
                    value={eventName}
                    onChange={(e) => setEventName(e.target.value)}
                    required
                />
                <textarea
                    placeholder="설명"
                    value={description}
                    onChange={(e) => setDescription(e.target.value)}
                />
                </div>
                
                {city && <p>선택된 도시: {city}</p>}
                {startDate && endDate && <p>선택된 날짜: {new Date(startDate).toLocaleDateString()} - {new Date(endDate).toLocaleDateString()}</p>}
                <button type="submit" className="learn-more">선택 완료</button>
            </form>
        </div>
    );
};

export default EventDetail;
