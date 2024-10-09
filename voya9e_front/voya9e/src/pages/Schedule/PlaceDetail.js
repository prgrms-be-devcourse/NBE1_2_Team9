import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { useNavigate, useParams } from 'react-router-dom';
import './PlaceDetail.css'; // CSS 파일 가져오기

function PlaceDetail() {
    const { placeId } = useParams(); // URL에서 placeId 가져오기
    const [placeDetail, setPlaceDetail] = useState(null);
    const [error, setError] = useState(null); // 에러 상태 추가
    const navigate = useNavigate(); // For navigation
    const [showHours, setShowHours] = useState(false); // 영업 시간 표시 상태

    useEffect(() => {
        // 장소 상세 정보 조회 API 호출
        const fetchPlaceDetail = async () => {
            try {
                const response = await axios.get(`/api/locations/${placeId}`);
                setPlaceDetail(response.data);
            } catch (error) {
                console.error('Error fetching place details:', error);
                setError(error); // 에러 상태 설정
            }
        };

        fetchPlaceDetail();
    }, [placeId]); // placeId 변경 시 재조회

    // 지도를 초기화하는 함수
    const initMap = (latitude, longitude) => {
        const center = { lat: latitude, lng: longitude };
        const newMap = new window.google.maps.Map(document.getElementById('map'), {
            zoom: 15,
            center: center,
        });

        new window.google.maps.Marker({
            position: center,
            map: newMap,
            icon: {
                path: window.google.maps.SymbolPath.CIRCLE,
                fillColor: '#FF5733',
                fillOpacity: 0.8,
                strokeWeight: 2,
                strokeColor: '#ffffff',
                scale: 13,
            },
        });
    };

    useEffect(() => {
        if (placeDetail) {
            initMap(placeDetail.latitude, placeDetail.longitude); // 장소의 위도와 경도를 사용하여 지도 초기화
        }
    }, [placeDetail]);

    if (error) {
        return <div className="error">장소 정보를 불러오는 중 오류가 발생했습니다. 나중에 다시 시도해주세요.</div>; // 에러 상태
    }

    if (!placeDetail) {
        return <div className="loading">Loading...</div>; // 로딩 상태
    }
    const handleBack = () => {
        navigate(-1); // 이전 페이지로 이동
    };

    return (
        <div className="place-detail">
            <header className="header">
                <h1 style={{ marginBottom: '5px' }}>Voyage</h1>
                <div className="button-container"> {/* 버튼을 감싸는 div 추가 */}
                    <button
                        className="currency-button"
                        onClick={() => {/* 환율 변환 기능 추가 */}}
                    > 환율
                    </button>
                    <button className="small-button-detail" onClick={handleBack}>뒤로가기</button>
                </div>
            </header>

            <div className="detail-content">
                <h2 style={{ marginBottom: '5px' }}>{placeDetail.name}</h2>
                <p className="address" style={{ marginTop: '0px' }}>{placeDetail.formatted_address}</p>
                {/* 장소 사진 */}
                {placeDetail.photoUrl && (
                    <img
                        src={placeDetail.photoUrl}
                        alt={placeDetail.name}
                        className="place-image"
                    />
                )}
                <p style={{ marginTop: '0px',marginBottom: '0px' }}>평점: {placeDetail.rating} ⭐</p>

                <div style={{ display: 'flex', alignItems: 'center' }}>
                    <p className={`open-now ${placeDetail.open_now ? 'open' : 'closed'}`}>
                        {placeDetail.open_now ? '현재 영업 중' : '현재 영업 종료'}
                    </p>
                    {/* 영업시간 토글 버튼 */}
                    {placeDetail.weekday_text && (
                        <>
                            <span style={{ fontSize: '0.9em', marginLeft: '10px' }}>영업시간:</span>
                            <button
                                onClick={() => setShowHours(!showHours)}
                                style={{ marginLeft: '5px', fontSize: '0.9em' }}
                                className="toggle-button"
                            >
                                {showHours ? '숨기기' : '보기'}
                            </button>
                        </>
                    )}
                </div>

                {/* 영업시간 표시 */}
                {showHours && placeDetail.weekday_text && (
                    <ul style={{ listStyleType: 'none', padding: 0 }}>
                        {placeDetail.weekday_text.split(', ').map((day, index) => (
                            <li key={index} style={{ fontSize: '0.9em', marginBottom: '5px' }}>{day}</li>
                        ))}
                    </ul>
                )}

                {/* 홈페이지 링크 */}
                <p style={{ marginBottom: '10px' }}>
                    <a href={placeDetail.website} target="_blank" rel="noopener noreferrer" className="learn-more">
                        홈페이지
                    </a>
                </p>

                {/* 길찾기 및 더 알아보기 링크 가로 배치 */}
                <div style={{ display: 'flex', gap: '15px' }}>
                    <p>
                        <a
                            href={`https://www.google.com/maps/dir/?api=1&destination=${placeDetail.latitude},${placeDetail.longitude}`}
                            target="_blank"
                            rel="noopener noreferrer"
                            className="learn-more"
                        >
                            길찾기
                        </a>
                    </p>
                    <p>
                        <a href={placeDetail.url} target="_blank" rel="noopener noreferrer" className="learn-more">
                            더 알아보기
                        </a>
                    </p>
                </div>
            </div>

            {/* 지도 표시 영역 */}
            <div id="map" style={{ height: '300px', width: '100%', marginTop: '20px' }}></div> {/* 지도 높이 조정 */}
        </div>
    );
}

export default PlaceDetail;
