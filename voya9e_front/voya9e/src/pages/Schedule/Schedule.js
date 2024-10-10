import React, { useState, useEffect } from 'react';
import { useNavigate,useParams } from 'react-router-dom';
import { Stomp } from '@stomp/stompjs'; // STOMP 가져오기
import SockJS from 'sockjs-client';    // SockJS 가져오기
import './Schedule.css';

const startTime = 6; // 시작 시간
const endTime = 25;  // 종료 시간

// 날짜 생성 함수
const generateDates = (startDate, endDate) => {
    const dates = [];
    let currentDate = new Date(startDate);
    const end = new Date(endDate);

    while (currentDate <= end) {
        dates.push(currentDate.toISOString().split('T')[0]);
        currentDate.setDate(currentDate.getDate() + 1);
    }
    return dates;
};

const calculateAverageHour = (startTime, endTime) => {
    const startHour = startTime.getHours() + startTime.getMinutes() / 60;
    const endHour = endTime.getHours() + endTime.getMinutes() / 60;
    return Math.floor((startHour + endHour) / 2);
};

// 시간 행 생성 함수
const generateTimeRows = (startTime, endTime, dates, events, handleMouseEvents) => {
    const rows = [];

    for (let hour = startTime; hour < endTime; hour++) {
        const formattedHour = String(hour).padStart(2, '0');
        const row = [];
        row.push(<td key={`time-${formattedHour}`}>{`${formattedHour}:00`}</td>);

        dates.forEach((date) => {
            const cellKeyHour = `${date}-${formattedHour}-00`;
            const cellKeyHalfHour = `${date}-${formattedHour}-30`;

            // 이벤트 확인
            const event = events.find(event => {
                if (event && event.visitStartTime && event.visitEndTime) {
                    const eventDate = event.visitStartTime.split('T')[0];
                    const eventStart = new Date(event.visitStartTime);
                    const eventEnd = new Date(event.visitEndTime);
                    return eventDate === date && eventStart <= new Date(`${date}T${formattedHour}:00`) && eventEnd > new Date(`${date}T${formattedHour}:00`);
                }
                return false; // event가 유효하지 않으면 false 반환
            });

            const isHalfHourEvent = events.find(event => {
                const eventDate = event.visitStartTime.split('T')[0];
                const eventStart = new Date(event.visitStartTime);
                const eventEnd = new Date(event.visitEndTime);
                return eventDate === date && eventStart <= new Date(`${date}T${formattedHour}:30`) && eventEnd > new Date(`${date}T${formattedHour}:30`);
            });

            const hourCellClass = event ? 'date-cell selected' : 'date-cell';
            const halfHourCellClass = isHalfHourEvent ? 'date-cell selected' : 'date-cell';

            const cells = (
                <td key={`${cellKeyHour}-${cellKeyHalfHour}`} className='date-cell'>
                    <div className="event-name" style={{ position: 'absolute', left: '50%', top: '50%', transform: 'translate(-50%, -50%)' }}>
                        {/* 이벤트가 있는 경우 셀 위에 장소 이름 표시 */}
                        {event && (
                            hour === calculateAverageHour(new Date(event.visitStartTime), new Date(event.visitEndTime)) && <span>{event.location.placeName}</span>
                        )}
                    </div>

                    <div
                        className={hourCellClass}
                        data-time={`${formattedHour}:00`}
                        data-date={date}
                        onMouseDown={handleMouseEvents.onMouseDown}
                        onMouseOver={handleMouseEvents.onMouseOver}
                        onMouseUp={handleMouseEvents.onMouseUp}
                    >
                    </div>
                    <div
                        className={halfHourCellClass}
                        data-time={`${formattedHour}:30`}
                        data-date={date}
                        onMouseDown={handleMouseEvents.onMouseDown}
                        onMouseOver={handleMouseEvents.onMouseOver}
                        onMouseUp={handleMouseEvents.onMouseUp}
                    />
                </td>
            );

            row.push(cells);
        });

        rows.push(<tr key={`row-${formattedHour}`}>{row}</tr>);
    }

    return rows;
};

const Schedule = () => {
    const [dates, setDates] = useState([]);
    const [isDragging, setIsDragging] = useState(false);
    const [selectedCells, setSelectedCells] = useState([]);
    const [events, setEvents] = useState([]); // 이벤트를 저장할 상태
    const [stompClient, setStompClient] = useState(null); // STOMP 클라이언트 상태 추가
    const { eventId } = useParams();
    const navigate = useNavigate();

    useEffect(() => {
        const fetchEventData = async () => {
            try {
                const response = await fetch(`/events/${eventId}`);
                const eventData = await response.json();
                
                if (eventData) {
                    const { startDate, endDate } = eventData;
                    const generatedDates = generateDates(startDate, endDate);
                    setDates(generatedDates);
                }
            } catch (error) {
                console.error('이벤트 데이터를 불러오는 중 오류 발생:', error);
            }
        };

        fetchEventData();
        fetchSavedEvents(); // 페이지 로드 시 저장된 이벤트 가져오기

        // 웹소켓 연결 설정
        const socket = new SockJS('http://localhost:8080/ws/schedule'); // 웹소켓 서버 URL
        const client = Stomp.over(socket); // STOMP 클라이언트 생성

        client.connect({}, (frame) => {
            console.log('웹소켓 연결 성공:', frame);

            client.subscribe('/topic/selectedCells', (message) => {
                const data = JSON.parse(message.body);
                console.log('받은 메시지:', data);
                handleReceivedSelection(data); // 받은 데이터를 처리하는 함수 호출
            });
        });

        setStompClient(client); // STOMP 클라이언트 상태 설정

        return () => {
            if (client) {
                client.disconnect(); // 컴포넌트 언마운트 시 STOMP 클라이언트 종료
            }
        };
    }, []);

    // 선택된 셀 정보를 처리하는 함수
    const handleReceivedSelection = (selectionData) => {
        console.log('선택된 셀 정보:', selectionData);

        const { date, startTime, endTime } = selectionData;

        const startHour = parseInt(startTime.split(':')[0]);
        const endHour = parseInt(endTime.split(':')[0]);

        // 선택된 셀을 업데이트하여 UI에 반영
        for (let hour = startHour; hour <= endHour; hour++) {
            const formattedHour = String(hour).padStart(2, '0');

            // 각 시간대에 맞는 셀을 찾아 'selected' 클래스 추가
            const cell = document.querySelector(`[data-date="${date}"][data-time="${formattedHour}:00"]`);
            const halfCell = document.querySelector(`[data-date="${date}"][data-time="${formattedHour}:30"]`);

            if (cell) {
                cell.classList.add('selected'); // 선택된 셀 배경색을 변경
                cell.style.backgroundColor = '#ffbf0052'; // 배경색 지정 (선택 중인 셀에 대해 변경)
                cell.style.pointerEvents = 'none'; // 드래그 불가능하게 설정
            }

            if (halfCell) {
                halfCell.classList.add('selected');
                halfCell.style.backgroundColor = '#ffbf0052'; // 배경색 지정 (선택 중인 셀에 대해 변경)
                halfCell.style.pointerEvents = 'none'; // 드래그 불가능하게 설정
            }
        }
    };


    // 저장된 이벤트를 가져오는 함수
    const fetchSavedEvents = async () => {
        try {
            const response = await fetch(`/events/${eventId}/locations`);
            const data = await response.json();
            console.log("data", data); // 데이터를 로그에 출력하여 확인
            
            // 이벤트가 있는 경우에만 상태 업데이트
            if (data && Array.isArray(data) && data.length > 0) {
                const events = data; // 이미 배열이므로 그대로 사용
                console.log("events", events); // 변환된 events를 로그에 출력하여 확인
                setEvents(events);
            } else {
                console.log("이벤트가 없습니다."); // 이벤트가 없는 경우 로그에 출력
            }
        } catch (error) {
            console.error('이벤트를 가져오는 중 오류 발생:', error);
        }
    };

    const handleMouseDown = (e) => {
        const targetDiv = e.target;

        // 이미 선택된 경우 드래그 시작하지 않음
        if (targetDiv.classList.contains('selected')) {
            return; // 드래그 시작하지 않음
        }

        setIsDragging(true);
        setSelectedCells([targetDiv]);
        handleDivSelection(targetDiv); // 선택 상태 업데이트
        e.preventDefault();
    };

    const handleMouseOver = (e) => {
        if (isDragging) {
            const targetDiv = e.target;

            // 현재 마우스가 있는 셀과 처음 선택된 셀의 날짜가 같을 때만 드래그가 가능하게 만듦
            const initialDate = selectedCells.length > 0 ? selectedCells[0].dataset.date : null;
            const targetDate = targetDiv.dataset.date;

            // 날짜가 같고, div 요소인지 확인하고 이미 선택된 경우 드래그 하지 않음
            if (targetDate === initialDate && targetDiv.dataset.time && !targetDiv.classList.contains('selected')) {
                handleDivSelection(targetDiv);
                if (!selectedCells.includes(targetDiv)) {
                    setSelectedCells((prev) => [...prev, targetDiv]);
                }
            }
        }
    };


    const handleMouseUp = () => {
        setIsDragging(false);

        if (selectedCells.length > 0) {
            selectedCells.forEach((cell) => {
                cell.classList.add('selected'); // div에 클래스 추가
            });

            const midIndex = Math.floor((selectedCells.length - 1) / 2);
            const middleCell = selectedCells[midIndex];
            const selectedDate = middleCell.dataset.date;
            const startTime = selectedCells[0].dataset.time;
            const endTime = selectedCells[selectedCells.length - 1].dataset.time;

            // 선택된 셀 정보를 서버로 STOMP를 통해 전송
            const selectionData = {
                date: selectedDate,
                startTime: startTime,
                endTime: endTime,
            };

            // STOMP를 통해 서버로 메시지 전송
            if (stompClient && stompClient.connected) {
                stompClient.send('/app/selectCell', {}, JSON.stringify(selectionData));
                console.log('전송한 데이터:', selectionData); // 전송 데이터 로그
            } else {
                console.error('STOMP 클라이언트가 연결되어 있지 않습니다.'); // 오류 로그
            }

            navigate(`/scheduledetail?date=${selectedDate}&startTime=${startTime}&endTime=${endTime}&eventId=${eventId}`);
        }
    };

    const handleDivSelection = (div) => {
        // div 요소에 'selected' 클래스 추가/제거
        div.classList.toggle('selected'); // div에 클래스 토글
    };

    const handleMouseEvents = {
        onMouseDown: handleMouseDown,
        onMouseOver: handleMouseOver,
        onMouseUp: handleMouseUp,
    };

    return (
        <div className="container">
            <h1>여행 일정표</h1>
            <div className="selection-info">
                <button className="selection-btn-end"></button>
                <span className="selection-text">선택완료</span>
                <button className="selection-btn"></button>
                <span className="selection-text">선택 중</span>
            </div>
            <table>
                <thead>
                <tr id="date-row">
                    <th>시간대</th>
                    {dates.map((date, index) => (
                        <th key={`date-${index}`}>{date}</th>
                    ))}
                </tr>
                </thead>
                <tbody id="schedule-body">
                {generateTimeRows(startTime, endTime, dates, events, handleMouseEvents)}
                </tbody>
            </table>
        </div>
    );
};

export default Schedule;
