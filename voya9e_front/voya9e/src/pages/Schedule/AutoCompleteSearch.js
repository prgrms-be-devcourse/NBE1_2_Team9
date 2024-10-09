import React, { useState } from 'react';
import axios from 'axios';
import { useNavigate, useParams } from 'react-router-dom';
import './AutoCompleteSearch.css'; // Add appropriate styling

function AutoCompleteSearch() {
    const [query, setQuery] = useState('');
    const [suggestions, setSuggestions] = useState([]);
    const [selectedPlace, setSelectedPlace] = useState(null); // 선택된 장소 상태
    const navigate = useNavigate(); // For navigation
    const { eventId } = useParams(); // URL 경로 파라미터에서 eventId 가져오기

    // Fetch suggestions based on user input
    const fetchSuggestions = (input) => {
        console.log("입력값:", input);
        console.log("이벤트 아이디:", eventId);
        if (input.length > 0 && eventId) {
            console.log("요청 진행:", input);
            axios.get(`/api/locations/${eventId}/autocomplete`, { params: { input } })
                .then(response => {
                    setSuggestions(response.data);
                })
                .catch(error => {
                    console.error('Error fetching suggestions:', error);
                    setSuggestions([]);
                });
        } else {
            setSuggestions([]);
        }
    };

    // Handle input change
    const handleInputChange = (e) => {
        const inputValue = e.target.value;
        setQuery(inputValue);
        fetchSuggestions(inputValue);
    };

    // Handle place selection toggle (선택 버튼 클릭 시 호출)
    const toggleSelection = (place) => {
        setSelectedPlace(selectedPlace === place ? null : place); // 같은 장소 선택 시 해제
    };

    // Handle "선택 완료" button click
    const saveSelection = () => {
        if (selectedPlace) {
            console.log('선택된 장소:', selectedPlace);

            axios.get(`/api/locations/${selectedPlace.placeId}`)
                .then(response => {
                    const placeDetail = response.data;

                    // API 응답에서 장소 정보를 포함한 locationData 생성
                    const locationData = {
                        placeName: placeDetail.name,
                        latitude: placeDetail.latitude,
                        longitude: placeDetail.longitude,
                        address: placeDetail.formatted_address,
                        rating: placeDetail.rating || 0,
                        photo: placeDetail.photoUrl
                    };

                    console.log("장소 전달될 정보:", locationData);

                    // locationData를 sessionStorage에 저장
                    sessionStorage.setItem('locationData', JSON.stringify(locationData));

                    // 이전 페이지로 이동
                    navigate(-1);
                })
                .catch(error => {
                    console.error('장소 상세 정보 가져오는 중 오류 발생:', error);
                    alert('장소 상세 정보 가져오기에 실패했습니다.');
                });
        } else {
            alert('장소를 선택하세요.');
        }
    };

    // Navigate to recommendation search page
    const goToRecommendationSearch = () => {
        navigate(`/recommended/${eventId}`); // 장소 추천 페이지로 이동
    };

    // Navigate to place detail page on place click
    const handlePlaceClick = (placeId) => {
        navigate(`/places/${placeId}`); // 장소 상세 페이지로 이동
    };

    // Handle back button click
    const handleBack = () => {
        navigate(-1); // 이전 페이지로 이동
    };

    return (
        <div className="auto-complete-search">
            <div className="header">
                <h1 className="title">Voyage</h1>
                <button className="small-button-auto" onClick={handleBack}>뒤로가기</button>
            </div>

            {/* 검색창 */}
            <div className="search-bar">
                <input
                    type="text"
                    value={query}
                    onChange={handleInputChange}
                    placeholder="맛집/관광지/숙소 검색"
                />
            </div>

            {/* 추천 장소 검색 버튼 */}
            <button onClick={goToRecommendationSearch} className="recommendation-btn">
                장소 추천
            </button>

            {/* Suggestions list */}
            <div className="result-list">
                {suggestions.length === 0 ? (
                    <div className="empty-result">검색 결과가 없습니다</div>
                ) : (
                    suggestions.map((suggestion) => (
                        <div
                            key={suggestion.placeId}
                            className={`result-item-auto ${selectedPlace === suggestion ? 'selected' : ''}`}
                            onClick={() => handlePlaceClick(suggestion.placeId)} // 클릭 시 상세 페이지로 이동
                        >
                            <span className="city-name">{suggestion.name}</span>
                            <button
                                className={`select-button-auto ${selectedPlace === suggestion ? 'selected' : ''}`}
                                onClick={(e) => {
                                    e.stopPropagation(); // 상세 페이지로 이동 방지
                                    toggleSelection(suggestion); // 장소 선택
                                }}
                            >
                                {selectedPlace === suggestion ? '취소' : '선택'}
                            </button>
                        </div>
                    ))
                )}
            </div>

            {/* 선택 완료 버튼 */}
            <button className="save-button" onClick={saveSelection}>선택 완료</button>

        </div>
    );
}

export default AutoCompleteSearch;
