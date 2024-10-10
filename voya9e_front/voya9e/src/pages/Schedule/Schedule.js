import React, { useState, useEffect } from 'react';
import { useNavigate,useParams } from 'react-router-dom';
import { useNotification } from '../../context/NotificationContext'; // NotificationProvider 사용
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
    const { selectedCell } = useNotification();
    const { deletedCell } = useNotification(); 
    const [events, setEvents] = useState([]);
    const { stompClient } = useNotification();
    const { eventId } = useParams();
    const [locations, setLocations] = useState([]);
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
        fetchLocations(); // 장소 데이터 가져오기
    }, [eventId]);
    
    // WebSocket에서 받은 선택된 셀 데이터 반영
    useEffect(() => {
        if (selectedCell) {
            console.log('WebSocket으로 받은 선택 셀 데이터:', selectedCell);
            handleReceivedSelection(selectedCell);
        }
    }, [selectedCell]);

    //WebSocket에서 받은 삭제된 셀 데이터 반영
    useEffect(() => {
        if (deletedCell) {
            handleRemoveSelection(deletedCell)
            
        }
    }, [deletedCell]);

    const handleRemoveSelection = (selectionData) => {
        console.log('삭제할 셀 정보:', selectionData);

        const { date, startTime, endTime } = selectionData;

        const startHour = parseInt(startTime.split(':')[0]);
        const endHour = parseInt(endTime.split(':')[0]);

        for (let hour = startHour; hour <= endHour; hour++) {
            const formattedHour = String(hour).padStart(2, '0');

            // 각 시간대에 맞는 셀을 찾아 'selected' 클래스 제거
            const cell = document.querySelector(`[data-date="${date}"][data-time="${formattedHour}:00"]`);
            const halfCell = document.querySelector(`[data-date="${date}"][data-time="${formattedHour}:30"]`);

            if (cell) {
                cell.classList.remove('selected');
                cell.style.backgroundColor = '';
                cell.style.pointerEvents = ''; // 드래그 가능하게
            }

            if (halfCell) {
                halfCell.classList.remove('selected');
                halfCell.style.backgroundColor = '';
                halfCell.style.pointerEvents = ''; 
            }
        }
    };
    
    const handleReceivedSelection = (selectionData) => {
        console.log('선택된 셀 정보:', selectionData);
    
        const { date, startTime, endTime } = selectionData;
    
        const startHour = parseInt(startTime.split(':')[0]);
        const endHour = parseInt(endTime.split(':')[0]);
    
        // 선택된 셀을 업데이트하여 UI에 반영
        for (let hour = startHour; hour <= endHour; hour++) {
            const formattedHour = String(hour).padStart(2, '0');
    
            const cell = document.querySelector(`[data-date="${date}"][data-time="${formattedHour}:00"]`);
            const halfCell = document.querySelector(`[data-date="${date}"][data-time="${formattedHour}:30"]`);
    
            if (cell) {
                cell.classList.add('selected');
                cell.style.backgroundColor = '#ffbf0052';
                cell.style.pointerEvents = 'none';
            }
    
            if (halfCell) {
                halfCell.classList.add('selected');
                halfCell.style.backgroundColor = '#ffbf0052';
                halfCell.style.pointerEvents = 'none';
            }
        }
    };
    
    // 저장된 이벤트를 가져오는 함수
    const fetchSavedEvents = async () => {
        try {
            const response = await fetch(`/events/${eventId}/locations`);
            const data = await response.json();
            
            // 이벤트가 있는 경우에만 상태 업데이트
            if (data && Array.isArray(data) && data.length > 0) {
                const events = data;
                setEvents(events);
            } else {
                console.log("이벤트가 없습니다.");
            }
        } catch (error) {
            console.error('이벤트를 가져오는 중 오류 발생:', error);
        }
    };

    // 날짜 버튼 클릭 시 장소 데이터 가져오기
    const handleDateButtonClick = async (date) => {
        await fetchLocations(date);
    };
    
    // 장소 데이터 가져오기
    const fetchLocations = async (date) => {
        try {
            const response = await fetch(`/events/${eventId}/locationsByDate?date=${date}`);
            const data = await response.json();
            setLocations(data.map(item => item.location)); // 가져온 장소 데이터 저장
        } catch (error) {
            console.error("Error fetching locations:", error);
        }
    };
    
    // locations 상태가 업데이트되면 initMap을 호출
    useEffect(() => {
        if (locations.length > 0) {
            initMap();
        }
    }, [locations]);
    
    const initMap = () => {
        console.log("locaiontt", locations);
        const map = new window.google.maps.Map(document.getElementById("map"), {
            zoom: 12,
            center: { lat: locations[0]?.latitude || 0, lng: locations[0]?.longitude || 0 },
        });
    
        const pathArray = [];

        locations.forEach((location, index) => {
            const markerLabel = (index + 1).toString(); // 마커 라벨 설정
            const marker = new window.google.maps.Marker({
                position: { lat: location.latitude, lng: location.longitude },
                map: map,
                label: markerLabel,
            });
            // 경로 배열에 추가
            pathArray.push(marker.getPosition());
        });
    
        // 경로 그리기
        const polyline = new window.google.maps.Polyline({
            path: pathArray,
            geodesic: true,
            strokeColor: '#FF0000',
            strokeOpacity: 1.0,
            strokeWeight: 2,
        });
    
        polyline.setMap(map); // 맵에 경로 추가
    };
    
    const handleMouseDown = (e) => {
        const targetDiv = e.target;

        // 이미 선택된 경우 드래그 시작하지 않음
        if (targetDiv.classList.contains('selected')) {
            return;
        }

        setIsDragging(true);
        setSelectedCells([targetDiv]);
        handleDivSelection(targetDiv); // 선택 상태 업데이트
        e.preventDefault();
    };

    const handleMouseOver = (e) => {
        if (isDragging) {
            const targetDiv = e.target;

            // 현재 마우스가 있는 셀과 처음 선택된 셀의 날짜가 같을 때만 드래그 가능
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
                cell.classList.add('selected');
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

            if (stompClient && stompClient.connected) {
                stompClient.publish({
                    destination: '/app/selectCell',
                    body: JSON.stringify(selectionData),
                });
            }

            navigate(`/scheduledetail?date=${selectedDate}&startTime=${startTime}&endTime=${endTime}&eventId=${eventId}`);
        }
    };

    const handleDivSelection = (div) => {
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
            <div id="map" style={{ height: '400px', width: '100%' }}></div> {}
            <div className="date-buttons">
                {dates.map((date) => (
                    <button key={date} onClick={() => handleDateButtonClick(date)}>
                        {date}
                    </button>
                ))}
            </div>
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
