import React, { useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom'; // useNavigate로 변경
import './CitySearch.css'; // 별도의 CSS 파일 사용

function CitySearch() {
    const navigate = useNavigate(); // useNavigate 훅 사용
    const [searchType, setSearchType] = useState('city'); // 'city' 또는 'country'
    const [query, setQuery] = useState('');
    const [results, setResults] = useState([]);
    const [selectedCity, setSelectedCity] = useState(null); // 선택된 도시를 저장하는 상태

    // API 검색 호출 함수
    const search = (query) => {
        const url = searchType === 'city' ? '/api/cities/search' : '/api/cities/country';
        axios.get(url, { params: { input: query } })
            .then(response => {
                setResults(response.data); // 결과를 목록에 바로 반영
            })
            .catch(error => {
                console.error('Error fetching data:', error);
            });
    };

    // 검색어가 변경될 때마다 API 호출 (도시 검색일 경우)
    const handleInputChange = (e) => {
        const inputValue = e.target.value;
        setQuery(inputValue);

        if (searchType === 'city' && inputValue.length > 0) {
            search(inputValue); // 입력이 있을 때마다 검색 (도시 검색일 때만)
        } else if (inputValue.length === 0) {
            setResults([]); // 검색어가 없으면 목록을 비움
        }
    };

    // 검색 타입이 변경될 때 (도시 검색 <-> 나라별 도시 검색), 검색어와 결과 초기화
    const handleSearchTypeChange = (type) => {
        setSearchType(type);
        setQuery(''); // 검색창 초기화
        setResults([]); // 검색 결과 초기화
        setSelectedCity(null); // 선택된 도시 초기화
    };

    // 도시 선택/취소 (단일 선택)
    const toggleSelection = (city) => {
        const cityName = searchType === 'city' ? city.split(',')[0] : city; // 도시 검색일 때만 ',' 기준으로 첫 번째 텍스트 가져오기
        setSelectedCity(cityName === selectedCity ? null : cityName); // 같은 도시 선택 시 해제
    };

    // 선택 완료 버튼 클릭 시
    const saveSelection = () => {
        if (selectedCity) {
            console.log('선택된 도시:', selectedCity);

            // 캘린더 페이지로 이동하면서 선택된 도시 정보 전달
            navigate('/calendar', { state: { selectedCity } }); // navigate로 변경
        } else {
            alert('도시를 선택하세요.'); // 선택되지 않았을 경우 경고창 표시
        }
    };

    // 나라별 검색 버튼 클릭 시
    const handleSearchButtonClick = () => {
        if (searchType === 'country' && query.length > 0) {
            search(query); // 검색 버튼을 눌렀을 때 API 호출 (나라별 검색일 때)
        }
    };

    return (
        <div className="city-search">
            <h1>voyage</h1>

            {/* 검색창 */}
            <div className="search-bar">
                <input
                    type="text"
                    value={query}
                    onChange={handleInputChange}
                    placeholder="도시 또는 나라 검색"
                />
                {searchType === 'country' && (
                    <button onClick={handleSearchButtonClick} className="search-button">
                        검색
                    </button> // 나라별 검색일 때만 검색 버튼 표시
                )}
            </div>

            {/* 검색 타입 선택 */}
            <div className="search-type-options">
                <button className={searchType === 'city' ? 'selected' : ''} onClick={() => handleSearchTypeChange('city')}>
                    도시
                </button>
                <button className={searchType === 'country' ? 'selected' : ''} onClick={() => handleSearchTypeChange('country')}>
                    나라별 도시
                </button>
            </div>

            {/* 검색 결과 목록 */}
            <div className="result-list">
                {results.length === 0 ? (
                    <div className="empty-result">검색 결과가 없습니다</div>
                ) : (
                    results.map((result) => {
                        const cityName = searchType === 'city' ? result.description.split(',')[0] : result.description; // 도시 검색일 때만 ',' 기준으로 첫 번째 텍스트 가져오기
                        return (
                            <div
                                key={result.placeId}
                                className={`result-item ${selectedCity === cityName ? 'selected' : ''}`} // 선택된 상태에 따라 배경 색상 변경
                                onClick={(e) => e.stopPropagation()} // 클릭 시 상세 정보 이동 방지
                            >
                                <span className="city-name">{result.description}</span>
                                <button
                                    onClick={(e) => {
                                        e.stopPropagation(); // 클릭 시 상세 정보 이동 방지
                                        toggleSelection(cityName); // 선택 토글
                                    }}
                                    className={`select-button-city ${selectedCity === cityName ? 'selected' : ''}`} // 선택된 상태에 따라 배경 색상 변경
                                >
                                    {selectedCity === cityName ? '선택됨' : '선택'}
                                </button>
                            </div>
                        );
                    })
                )}
            </div>

            {/* 선택 완료 버튼 */}
            <button className="save-button" onClick={saveSelection}>
                선택 완료
            </button>
        </div>
    );
}

export default CitySearch;
