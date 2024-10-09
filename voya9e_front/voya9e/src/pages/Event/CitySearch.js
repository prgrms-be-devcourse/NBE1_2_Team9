import React, { useState } from 'react';
import axios from 'axios';
import { useNavigate, useLocation } from 'react-router-dom';
import './CitySearch.css';

function CitySearch() {
    const navigate = useNavigate();
    const location = useLocation(); // location에서 상태를 받아옵니다.
    const [searchType, setSearchType] = useState('city');
    const [query, setQuery] = useState('');
    const [results, setResults] = useState([]);
    const [selectedCity, setSelectedCity] = useState(null);
    const groupId = location.state?.groupId; // 이전 페이지에서 그룹 아이디를 가져옵니다.

    const search = (query) => {
        const url = searchType === 'city' ? '/api/cities/search' : '/api/cities/country';
        axios.get(url, { params: { input: query } })
            .then(response => {
                setResults(response.data);
            })
            .catch(error => {
                console.error('Error fetching data:', error);
            });
    };

    const handleInputChange = (e) => {
        const inputValue = e.target.value;
        setQuery(inputValue);

        if (searchType === 'city' && inputValue.length > 0) {
            search(inputValue);
        } else if (inputValue.length === 0) {
            setResults([]);
        }
    };

    const handleSearchTypeChange = (type) => {
        setSearchType(type);
        setQuery('');
        setResults([]);
        setSelectedCity(null);
    };

    const toggleSelection = (city) => {
        const cityName = searchType === 'city' ? city.split(',')[0] : city;
        setSelectedCity(cityName === selectedCity ? null : cityName);
    };

    // 선택 완료 버튼 클릭 시
    const saveSelection = () => {
        if (selectedCity) {
            // 선택된 도시를 sessionStorage에 저장
            sessionStorage.setItem('cityData', JSON.stringify({ selectedCity }));

            // groupId가 있는 경우 URL에 추가
            navigate(`/eventdetail/${groupId}`, { state: { selectedCity, groupId } });
        } else {
            alert('도시를 선택하세요.');
        }
    };

    const handleSearchButtonClick = () => {
        if (searchType === 'country' && query.length > 0) {
            search(query);
        }
    };

    return (
        <div className="city-search">
            <h1>voyage</h1>
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
                    </button>
                )}
            </div>

            <div className="search-type-options">
                <button className={searchType === 'city' ? 'selected' : ''} onClick={() => handleSearchTypeChange('city')}>
                    도시
                </button>
                <button className={searchType === 'country' ? 'selected' : ''} onClick={() => handleSearchTypeChange('country')}>
                    나라별 도시
                </button>
            </div>

            <div className="result-list">
                {results.length === 0 ? (
                    <div className="empty-result">검색 결과가 없습니다</div>
                ) : (
                    results.map((result) => {
                        const cityName = searchType === 'city' ? result.description.split(',')[0] : result.description;
                        return (
                            <div
                                key={result.placeId}
                                className={`result-item ${selectedCity === cityName ? 'selected' : ''}`}
                                onClick={(e) => e.stopPropagation()}
                            >
                                <span className="city-name">{result.description}</span>
                                <button
                                    onClick={(e) => {
                                        e.stopPropagation();
                                        toggleSelection(cityName);
                                    }}
                                    className={`select-button-city ${selectedCity === cityName ? 'selected' : ''}`}
                                >
                                    {selectedCity === cityName ? '선택됨' : '선택'}
                                </button>
                            </div>
                        );
                    })
                )}
            </div>

            <button className="save-button" onClick={saveSelection}>
                선택 완료
            </button>
        </div>
    );
}

export default CitySearch;
