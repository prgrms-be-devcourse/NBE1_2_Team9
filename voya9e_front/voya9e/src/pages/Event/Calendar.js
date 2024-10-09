import React, { useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import Calendar from 'react-calendar';
import 'react-calendar/dist/Calendar.css';
import './Calendar.css';

const CustomCalendar = () => {
    const location = useLocation();
    const navigate = useNavigate();
    const [dateRange, setDateRange] = useState([null, null]); // [start, end]
    const groupId = location.state?.groupId; // 이전 페이지에서 그룹 아이디를 가져옵니다.

    const handleDateChange = (range) => {
        const [start, end] = range;
        if (!end) {
            setDateRange([start, start]);
        } else {
            setDateRange([start, end]);
        }
    };

    const handleSave = () => {
        const [start, end] = dateRange;
        if (start && end) {
            console.log('시작 날짜:', start);
            console.log('종료 날짜:', end);

            // sessionStorage에 데이터 저장
            sessionStorage.setItem('dateData', JSON.stringify({ visitStart: start, visitEnd: end }));

            navigate(`/eventdetail/${location.state.groupId}`); // groupId를 URL로 전송
        } else {
            alert('시작 날짜와 종료 날짜를 모두 선택하세요.');
        }
    };

    return (
        <div className="calendar">
            <h1>여행 일정</h1>
            <div className="calendar-scroll-container">
                {Array.from({ length: 12 }).map((_, index) => {
                    const date = new Date();
                    date.setMonth(date.getMonth() + index);
                    return (
                        <div className="calendar-item" key={index}>
                            <h2 style={{ fontSize: '16px', color: '#555' }}>{date.toLocaleString('default', { month: 'long', year: 'numeric' })}</h2>
                            <Calendar
                                selectRange={true}
                                onChange={handleDateChange}
                                value={dateRange}
                                minDate={new Date()}
                                activeStartDate={date}
                                tileContent={({ date: tileDate }) => {
                                    const [start, end] = dateRange;
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
