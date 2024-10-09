import React, { useState, useEffect, useCallback } from 'react';
import axios from 'axios';
import { useNavigate,useParams  } from 'react-router-dom';
import './RecommendationSearch.css';

function RecommendationSearch() {
    const [placeType, setPlaceType] = useState('establishment');
    const [sortOption, setSortOption] = useState('rating');
    const [places, setPlaces] = useState([]);
    const [selectedPlace, setSelectedPlace] = useState(null); // 단일 선택된 장소
    const navigate = useNavigate(); // Initialize useNavigate

    // URL에서 eventId 가져오기
    const { eventId } = useParams(); // URL에서 eventId 가져옴
    console.log("eventId:",eventId)
    // Fetch recommended places
    const fetchRecommendations = useCallback((type, sort) => {
        if (!eventId) {
            console.error('eventId가 없습니다.');
            return;
        }
        axios.get(`/api/locations/${eventId}/recommend`, { params: { type } })
            .then(response => {
                let data = response.data;
                if (sort === 'rating') {
                    data.sort((a, b) => b.rating - a.rating);
                } else if (sort === 'reviews') {
                    data.sort((a, b) => b.user_ratings_total - a.user_ratings_total);
                }
                setPlaces(data);
                initMap(data); // Initialize map with new places data
            })
            .catch(error => {
                console.error('Error fetching recommendations:', error);
            });
    }, [eventId]);

    // Initialize Google Map
    const initMap = (placesData) => {
        if (!placesData || placesData.length === 0) return;

        const center = { lat: placesData[0].latitude, lng: placesData[0].longitude };
        const newMap = new window.google.maps.Map(document.getElementById('map'), {
            zoom: 15,
            center: center,
        });

        // Add markers to the map
        placesData.forEach(place => {
            new window.google.maps.Marker({
                position: { lat: place.latitude, lng: place.longitude },
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
        });
    };

    // Load default recommendations on page load
    useEffect(() => {
        fetchRecommendations(placeType, sortOption);
    }, [fetchRecommendations, placeType, sortOption]);

    // Handle place type change
    const handlePlaceTypeChange = (type) => {
        setPlaceType(type);
        fetchRecommendations(type, sortOption);
    };

    // Handle sort option change
    const handleSortChange = (sort) => {
        setSortOption(sort);
        fetchRecommendations(placeType, sort);
    };

    // Handle place selection
    const toggleSelection = (place) => {
        // 단일 선택 상태 업데이트
        setSelectedPlace(selectedPlace === place ? null : place);
    };

    // Navigate to place detail on item click
    const handlePlaceClick = (placeId) => {
        navigate(`/places/${placeId}`); // Navigate to the place detail page
    };

    // Handle selection completion
    const handleSelectionComplete = () => {
        if (!selectedPlace) {
            alert('선택된 장소가 없습니다.'); // 선택된 장소가 없을 경우 경고창 표시
        } else {
            console.log('선택된 장소:', selectedPlace);

            // Create location data with details from the selected place
            const locationData = {
                placeName: selectedPlace.name, // 선택된 장소 이름
                latitude: selectedPlace.latitude, // 선택된 장소의 위도
                longitude: selectedPlace.longitude, // 선택된 장소의 경도
                address: selectedPlace.address, // 선택된 장소의 주소
                rating: selectedPlace.rating || 0, // 선택된 장소의 평점 (기본값 0)
                photo: selectedPlace.photoUrl || null // 선택된 장소의 사진 (기본값 null)
            };
            console.log('보낼 장소데이터:', locationData);


            // locationData를 sessionStorage에 저장
            sessionStorage.setItem('locationData', JSON.stringify(locationData));

            // 이전 페이지로 이동
            navigate(-2); // 또는 navigate('/schedule-detail'); 로 이동
        }
    };

    const handleBack = () => {
        navigate(-1); // 이전 페이지로 이동
    };

    return (
        <div className="recommendation-search" style={{ display: 'flex', flexDirection: 'column', height: '100vh' }}>
            {/* 뒤로가기 버튼 및 추천 장소 텍스트 */}
            <div className="header">
                <h1 className="title-reco">추천장소</h1>
                <button className="small-button-auto" onClick={handleBack}>뒤로가기</button>
            </div>

            {/* Google Map */}
            <div id="map" style={{ height: '300px', width: '100%' }}></div> {/* 지도 높이 조정 */}

            {/* Place types and sort options */}
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <div className="place-types">
                    <button onClick={() => handlePlaceTypeChange('establishment')} className={placeType === 'establishment' ? 'selected' : ''}>모두</button>
                    <button onClick={() => handlePlaceTypeChange('restaurant')} className={placeType === 'restaurant' ? 'selected' : ''}>맛집</button>
                    <button onClick={() => handlePlaceTypeChange('tourist_attraction')} className={placeType === 'tourist_attraction' ? 'selected' : ''}>관광지</button>
                    <button onClick={() => handlePlaceTypeChange('lodging')} className={placeType === 'lodging' ? 'selected' : ''}>숙소</button>
                </div>

                {/* Sort options */}
                <div className="sort-options">
                    <button onClick={() => handleSortChange('rating')} className={sortOption === 'rating' ? 'selected' : ''}>평점순</button>
                    <button onClick={() => handleSortChange('reviews')} className={sortOption === 'reviews' ? 'selected' : ''}>리뷰순</button>
                </div>
            </div>

            {/* Recommended places */}
            <div className="place-list" style={{ flex: 1, overflowY: 'auto' }}>
                {places.map(place => (
                    <div
                        key={place.placeId}
                        className={`place-item ${selectedPlace === place ? 'selected' : ''}`} // 선택된 상태에 따라 배경 색상 변경
                        onClick={() => handlePlaceClick(place.placeId)} // Click handler
                    >
                        <div className="place-image-container">
                            <img src={place.photoUrl} alt={place.name} className="place-image-reco" />
                        </div>
                        <div style={{ flex: 1 }}> {/* 이 div에 flex 속성을 주어 공간을 차지하게 함 */}
                            <span className="place-name">{place.name}</span> {/* 이름에 스타일 적용 */}
                            <p>평점: {place.rating} | 리뷰 수: {place.user_ratings_total}</p>
                        </div>
                        <button
                            onClick={(e) => { e.stopPropagation(); toggleSelection(place); }} // 선택 버튼 클릭 시 상세 정보 이동 방지
                            className={`select-button-reco ${selectedPlace === place ? 'selected' : ''}`} // 선택된 상태에 따라 배경 색상 변경
                        >
                            {selectedPlace === place ? '취소' : '선택'}
                        </button>
                    </div>
                ))}
            </div>

            {/* 선택 완료 버튼 */}
            <button className="save-button" onClick={handleSelectionComplete}>
                선택 완료
            </button>
        </div>
    );
}

export default RecommendationSearch;
