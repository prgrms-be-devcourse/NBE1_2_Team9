import React, { useState } from 'react';

const WeatherSearch = ({ onSearch }) => {
  const [location, setLocation] = useState('');

  const handleSearch = () => {
    if (location) {
      onSearch(location);
    } else {
      alert('지역 이름을 입력하세요.');
    }
  };

  return (
    <div>
      <input
        type="text"
        value={location}
        onChange={(e) => setLocation(e.target.value)}
        placeholder="어디로 떠나시나요?"
      />
      &nbsp;&nbsp;
      <button onClick={handleSearch}>검색</button>
    </div>
  );
};

export default WeatherSearch;