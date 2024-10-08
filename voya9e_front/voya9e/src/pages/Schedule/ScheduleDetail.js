import React, { useState, useEffect } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import axios from 'axios';
import { useNotification } from '../../context/NotificationContext';
import './ScheduleDetail.css';

const ScheduleDetail = () => {
    const [searchParams] = useSearchParams();
    const [description, setDescription] = useState('');
    const [locationData, setLocationData] = useState(null);
    const navigate = useNavigate();
    const { stompClient } = useNotification();

    const eventId = searchParams.get('eventId');
    const selectedDate = searchParams.get('date'); // 선택된 날짜
    const startTime = searchParams.get('startTime'); // 선택된 시작시간
    const endTime = searchParams.get('endTime'); // 선택된 종료시간

    useEffect(() => {
        const storedLocationData = sessionStorage.getItem('locationData');
        if (storedLocationData) {
            setLocationData(JSON.parse(storedLocationData)); // 장소 정보 상태 설정
            console.log("전달받은 장소 정보:", JSON.parse(storedLocationData)); // 콘솔에 장소 정보 출력
        }
    }, []);

    const handlePlaceSearch = () => {
        navigate(`/autosearch/${eventId}`);
    };

    const handleBack = () => {
        sessionStorage.removeItem('locationData'); // 세션 스토리지 초기화

        const selectionData = {
            date: selectedDate,
            startTime: startTime,
            endTime: endTime,
        };

        if (stompClient && stompClient.connected) {
            stompClient.publish({
                destination: '/app/deleteCell',
                body: JSON.stringify(selectionData),
            });
            console.log('셀 삭제 정보 전송:', selectionData);
        }
        navigate(-1);
    };

    // 시간을 'HH:mm:ss' 형식으로 변환하는 함수 (한 자릿수 시간에 0을 붙임)
    const formatTime = (time) => {
        const [hour, minute] = time.split(':');
        return `${hour.padStart(2, '0')}:${minute.padStart(2, '0')}:00`; // 초는 00으로 고정
    };

    const handleSubmit = async () => {
        if (!locationData) {
            alert("장소 정보를 선택해 주세요.");
            return;
        }
        const locationReq = {
            placeName: locationData.placeName,
            latitude: locationData.latitude,
            longitude: locationData.longitude,
            address: locationData.address,
            rating: locationData.rating,
            photo: locationData.photo,
        };

        try {
            const locationResponse = await axios.post('/locations', locationReq);
            const locationId = locationResponse.data.locationId;

            // 시작 시간과 종료 시간을 'HH:mm:ss' 형식으로 변환
            const formattedStartTime = formatTime(startTime);
            const formattedEndTime = formatTime(endTime);

            // LocalDateTime 형식으로 변환 (YYYY-MM-DDTHH:mm:ss)
            const formattedStartDateTime = `${selectedDate}T${formattedStartTime}`;
            const formattedEndDateTime = `${selectedDate}T${formattedEndTime}`;

            // 이벤트와 장소 연결 요청
            const eventLocationReq = {
                eventId: Number(eventId),
                locationId: locationId,
                description,
                visitStartTime: formattedStartDateTime,
                visitEndTime: formattedEndDateTime,
            };
            const eventLocationResponse = await axios.post(`/events/${eventId}/locations`, eventLocationReq);
            console.log("이벤트와 장소:", eventLocationResponse.data);
            alert("일정이 저장되었습니다.");

            navigate(`/schedule/${eventId}`);
            sessionStorage.removeItem('locationData'); // 세션 스토리지 초기화

        } catch (error) {
            console.error("오류 발생:", error);
            alert("저장 중 오류가 발생했습니다.");
        }
    };

    return (
        <div className="schedule-detail">
            <div className="left-align">
                <button className="small-button" onClick={handleBack}>뒤로가기</button>
            </div>
            <h1 style={{ fontSize: '2em', marginLeft: '10px' }}>일정 추가</h1>
            <p>선택된 날짜:</p>
            <input
                type="text"
                value={selectedDate}
                readOnly
                className="readonly-input"
            />
            <p>시작 시간:</p>
            <input
                type="text"
                value={startTime}
                readOnly
                className="readonly-input"
            />
            <p>종료 시간:</p>
            <input
                type="text"
                value={endTime}
                readOnly
                className="readonly-input"
            />
            <div className="left-align">
                <button className="small-button" onClick={handlePlaceSearch}>장소 선택</button>
            </div>
            {locationData && (
                <div className="selected-location">
                    <h3>{locationData.placeName}</h3>
                </div>
            )}
            <textarea
                value={description}
                onChange={(e) => setDescription(e.target.value)}
                placeholder="일정에 대한 설명을 입력하세요"
            />
            <button className="save-button" onClick={handleSubmit}>저장</button>
        </div>
    );
};

export default ScheduleDetail;
