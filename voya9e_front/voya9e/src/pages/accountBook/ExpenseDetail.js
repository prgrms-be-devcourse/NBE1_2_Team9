import React, { useState, useEffect } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { useParams } from 'react-router-dom'; // URL 파라미터 가져오기
import axios from 'axios';
import 'bootstrap/dist/css/bootstrap.min.css';

function ExpenseDetail() {
  const location = useLocation();
  const navigate = useNavigate();

  const { state } = location || {};
  const { expenseId } = state || {}; // state에서 expenseId를 받아옴
  const { groupId } = useParams(); 


  const [errorMessage, setErrorMessage] = useState('');

  const [expenseDetail, setExpenseDetail] = useState(null);
  // const [expenseDetail, setExpenseDetail] = useState(
  //   {
  //       expensesId : 123,
  //       expenseDate : "2024-09-24T09:54:22",
  //       itemName : "food",
  //       amount : 1000,
  //       paidByUserId : "유저1",
  //       receiptImage : null
  //     }
  // ); //테스트용


  const url = "";
  useEffect(() => {
    console.log("Received expenseId:", expenseId); // 로그 추가

    const fetchExpenseDetail = async () => {
      try {
        const response = await axios.post(url+"/accountBook", {
          expenseId: expenseId // 요청 바디로 expenseId 전달
        }, {
          headers: {
            'Content-Type': 'application/json'
          }
        });

        if (response.status === 200) {
          setExpenseDetail(response.data);
        }
      } catch (error) {
        if (error.response && error.response.status === 404) {
          setErrorMessage('해당 지출 목록을 찾을 수 없습니다.');
        } else if (error.response && error.response.status === 401) {
          setErrorMessage('리소스에 대한 액세스 권한이 없습니다.');
        } else {
          setErrorMessage('지출 목록을 가져오는 중 오류가 발생했습니다.');
        }
      }
    };

    if (expenseId) {
      fetchExpenseDetail(); // expenseId가 있을 때만 API 호출
    } else {
      setErrorMessage('지출 항목 ID가 전달되지 않았습니다.');
    }
  }, [expenseId]);

  if (errorMessage) {
    return <div className="alert alert-danger">{errorMessage}</div>;
  }

  if (!expenseDetail) {
    return <div>로딩 중...</div>;
  }

  const handleAccountBook = () => {
    navigate(`/accountBook/${groupId}`);
  };

  const handleEditAccount = (expenseId) => {
    navigate(`/accountBook/${groupId}/edit`, { state: { expenseId } });
  };
  

  return (
    <div className="container mt-5">
      <h1>{expenseDetail.itemName}</h1>
      <div className="d-flex justify-content-between">
        <button className="btn btn-warning" onClick={() => handleEditAccount( expenseDetail.expensesId)}>수정</button>
        <button className="btn btn-success" onClick={() => handleAccountBook()}>확인</button>
      </div>

      <div className="mt-4">
        <p><strong>지출 시간:</strong> {expenseDetail.expensesDate.replace("T"," |  ")}</p>
        <p><strong>지출 항목:</strong> {expenseDetail.itemName}</p>
        <p><strong>지출 금액:</strong> {expenseDetail.amount.toLocaleString()}원</p>
        <p><strong>지출한 사용자:</strong> {expenseDetail.paidByUserId}</p>
      </div>
    </div>
  );
}

export default ExpenseDetail;
