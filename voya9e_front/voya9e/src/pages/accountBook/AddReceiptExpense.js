import React, { useState, useEffect } from 'react';
import { useLocation, useNavigate, useParams } from 'react-router-dom';
import axios from 'axios';
import './accountBook.css';

function AddReceiptExpense() {
  const location = useLocation();
  const navigate = useNavigate();
  const { groupId } = useParams(); // URL에서 groupId를 가져옴
  const { state } = location || {};
  
  const [data, setData] = useState(state?.data || null);
  const [selectedDateId, setSelectedDateId] = useState('');
  const [selectedAmountId, setSelectedAmountId] = useState('');
  const [errorMessage, setErrorMessage] = useState('');

  useEffect(() => {
    if (state?.data) {
      setData(state.data);
    }
  }, [state]);

  const handleDateSelect = (id) => {
    setSelectedDateId(id);
  };

  const handleAmountSelect = (id) => {
    setSelectedAmountId(id);
  };

  const handleSubmit = async () => {
    if (!selectedDateId || !selectedAmountId) {
      setErrorMessage('지출 날짜와 금액을 모두 선택해주세요.');
      return;
    }

    const URL = ""; // 백엔드 서버 URL로 설정

    try {
      const expenseDate = data[selectedDateId];
      const amount = data[selectedAmountId];

      // 선택된 날짜와 금액을 백엔드로 전송하여 포맷팅
      const response = await axios.post(URL+`/accountBook/receipt/formatting`, {
        expenseDate,
        amount,
      });

      if (response.status === 200) {
        const formattedData = response.data;
        // 포맷팅된 데이터를 새로운 페이지로 전달
        navigate(`/accountBook/${groupId}/addExpense`, { state: { ...formattedData } });
      }
    } catch (error) {
      console.error('포맷팅 중 오류 발생:', error);
      setErrorMessage('지출 정보를 포맷팅하는 중 오류가 발생했습니다.');
    }
  };

  return (
    <div className="container mt-5">
      <h1>처리된 영수증 정보</h1>

      {errorMessage && <p className="text-danger">{errorMessage}</p>}

      <div className="ocr-result">
        <ul>
          {Object.entries(data).map(([id, value]) => (
            <li key={id}>
              {value}
              <button className="btn btn-link btn-select" onClick={() => handleDateSelect(id)}>지출 날짜 선택</button>
              <button className="btn btn-link btn-select" onClick={() => handleAmountSelect(id)}>지출 금액 선택</button>
            </li>
          ))}
        </ul>
      </div>

      {/* 선택된 항목 표시 */}
      <div className="selected-info mt-4">
        <h5>선택된 지출 날짜:</h5>
        <p>{selectedDateId ? data[selectedDateId] : '선택된 항목이 없습니다.'}</p>
        <h5>선택된 지출 금액:</h5>
        <p>{selectedAmountId ? data[selectedAmountId] : '선택된 항목이 없습니다.'}</p>
      </div>

      <div className="form-group mt-4">
        <button className="btn btn-primary" onClick={handleSubmit}>선택 완료</button>
      </div>
    </div>
  );
}

export default AddReceiptExpense;
