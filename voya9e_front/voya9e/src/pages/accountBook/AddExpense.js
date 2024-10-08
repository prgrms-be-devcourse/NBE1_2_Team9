import React, { useState, useEffect } from 'react';
import axios from 'axios';
import 'bootstrap/dist/css/bootstrap.min.css';
import './accountBook.css';
import { useNavigate, useLocation, useParams } from 'react-router-dom';

function AddExpense() {
  const navigate = useNavigate();
  const location = useLocation();
  const { groupId } = useParams(); // URL 파라미터에서 groupId를 가져옴

  // 이전 페이지에서 선택한 데이터를 가져옴
  const { state } = location || {};
  const { expenseDate: initialExpenseDate, amount: initialAmount } = state || {};

  const [expenseDate, setExpenseDate] = useState(initialExpenseDate || '');
  const [itemName, setItemName] = useState('');
  const [amount, setAmount] = useState(initialAmount || '');
  const [paidByUserId, setPaidByUserId] = useState('');

  let URL="";

  useEffect(() => {
    if (initialExpenseDate) {
      setExpenseDate(initialExpenseDate);
    }
    if (initialAmount) {
      setAmount(initialAmount);
    }
  }, [initialExpenseDate, initialAmount]);

  const handleSubmit = async () => {
    // 필수 입력값 확인
    if (!itemName) {
      window.alert('지출 항목을 작성해주세요.');
      return; // 입력값이 없으면 함수 종료
    } else if (!amount) {
      window.alert('지출 금액을 작성해주세요.');
      return; // 입력값이 없으면 함수 종료
    } else if (!paidByUserId) {
      window.alert('지출한 사용자를 작성해주세요.');
      return; // 입력값이 없으면 함수 종료
    }

    const curr = new Date();
    const utc = curr.getTime() + (curr.getTimezoneOffset() * 60 * 1000);
    const KR_TIME_DIFF = 9 * 60 * 60 * 1000;

    const currentDateTime = expenseDate
      ? expenseDate.length === 16
        ? `${expenseDate}:00` // 사용자가 입력한 값이 'YYYY-MM-DDTHH:MM' 형식이면 초(:00) 추가
        : expenseDate // 사용자가 이미 초를 입력한 경우 그대로 사용
      : new Date(utc + KR_TIME_DIFF).toISOString().replace(/\..*/, ""); // 사용자가 입력 안 할 경우 현재 시간

    const expenseData = {
      expenseDate: currentDateTime,
      itemName,
      amount,
      paidByUserId
    };

    console.log(expenseData);

    try {
      const response = await axios.post(URL + `/accountBook/${groupId}`, expenseData, {
        headers: {
          'Content-Type': 'application/json'
        },
      });

      if (response.status === 201) {
        console.log("지출이 성공적으로 추가되었습니다.");
        navigate(`/accountBook/${groupId}`);
      }
    } catch (error) {
      console.error("지출 추가 중 오류 발생:", error);
      alert('지출 추가 중 오류 발생. 다시 시도해주세요');
      navigate(`/accountBook/${groupId}`);
    }
  };

  return (
    <div className="container mt-5">
      <h1 className="voyage-logo text-center">voyage</h1>
      <div className="card p-4 mt-4">
        <div className="form-group">
          <label>지출 날짜</label>
          <input
            type="datetime-local"
            className="form-control"
            value={expenseDate}
            onChange={(e) => setExpenseDate(e.target.value)}
            placeholder="YYYY-MM-DDTHH:MM:SS"
          />
          <small className="form-text text-muted">
            생략하면 지출을 작성한 날짜로 저장이 됩니다
          </small>
        </div>

        <div className="form-group mt-3">
          <label>지출 항목</label>
          <input
            type="text"
            className="form-control"
            value={itemName}
            onChange={(e) => setItemName(e.target.value)}
            placeholder="지출 항목을 작성해주세요 (e.g., 음식, 옷, 교통비)"
          />
        </div>

        <div className="form-group mt-3">
          <label>지출 금액</label>
          <input
            type="number"
            className="form-control"
            value={amount}
            onChange={(e) => setAmount(e.target.value)}
            placeholder="지출한 금액을 적어주세요"
          />
        </div>

        <div className="form-group mt-3">
          <label>지출한 사용자</label>
          <input
            type="text"
            className="form-control"
            value={paidByUserId}
            onChange={(e) => setPaidByUserId(e.target.value)}
            placeholder="지출한 사용자를 적어주세요"
          />
        </div>

        <button className="btn mt-4" onClick={handleSubmit}>
          추가하기
        </button>
      </div>
    </div>
  );
}

export default AddExpense;
