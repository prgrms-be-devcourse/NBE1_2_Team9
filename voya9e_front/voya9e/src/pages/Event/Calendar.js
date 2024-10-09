import React, { useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import Calendar from 'react-calendar'; // react-calendar import
import 'react-calendar/dist/Calendar.css'; // 기본 스타일 가져오기
import './Calendar.css';

const CustomCalendar = () => {
    const location = useLocation();
    const navigate = useNavigate();
    const { selectedCity } = location.state || {};
    const [dateRange, setDateRange] = useState([null, null]); // [start, end]

    const handleDateChange = (range) => {
        const [start, end] = range;

        // 종료 날짜가 없을 경우 시작 날짜와 종료 날짜를 동일하게 설정
        if (!end) {
            setDateRange([start, start]);
        } else {
            setDateRange([start, end]);
        }
    };

    const handleSave = () => {
        const [start, end] = dateRange;
        if (start && end) {
            console.log('선택한 도시:', selectedCity);
            console.log('시작 날짜:', start);
            console.log('종료 날짜:', end);

            navigate('/result', {
                state: {
                    selectedCity,
                    visitStart: start,
                    visitEnd: end,
                },
            });
        } else {
            alert('시작 날짜와 종료 날짜를 모두 선택하세요.');
        }
    };

    return (
        <div className="calendar">
            <h1>여행 일정</h1>
            {selectedCity && <p style={{ fontSize: '16px', color: '#555' }}>선택된 도시: {selectedCity}</p>}
            <div className="calendar-scroll-container">
                {Array.from({ length: 12 }).map((_, index) => {
                    const date = new Date();
                    date.setMonth(date.getMonth() + index); // 각 달의 날짜를 설정

                    return (
                        <div className="calendar-item" key={index}>
                            <h2 style={{ fontSize: '16px', color: '#555' }}>{date.toLocaleString('default', { month: 'long', year: 'numeric' })}</h2>
                            <Calendar
                                selectRange={true}
                                onChange={handleDateChange}
                                value={dateRange}
                                minDate={new Date()} // 오늘 이후의 날짜만 선택 가능
                                activeStartDate={date} // 각 달의 첫 번째 날짜를 설정
                                tileContent={({ date: tileDate }) => {
                                    const [start, end] = dateRange;

                                    // 선택된 날짜를 강조 표시
                                    if (start && end && tileDate >= start && tileDate <= end) {
                                        return <div className="highlighted">{tileDate.getDate()}</div>;
                                    }

                                    return null;
                                }}
                            />
                        </div>
                    );
                })}
            </div>
            <button className="save-button-cal" onClick={handleSave}>
                선택 완료
            </button>
        </div>
    );
};

export default CustomCalendar;
