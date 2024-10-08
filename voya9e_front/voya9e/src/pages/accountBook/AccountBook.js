import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom'; // URL 파라미터 가져오기
import 'bootstrap/dist/css/bootstrap.min.css'; // 스타일을 위한 bootstrap 추가
import axios from 'axios';
import './accountBook.css';
import { useNavigate } from 'react-router-dom';
import AddReceiptPopup from './AddReceiptPopup';

function AccountBook() {
    const [isPopupOpen, setIsPopupOpen] = useState(false); // 팝업 열림 여부 상태
    const navigate = useNavigate();
  
    // 팝업 열기
    const openPopup = () => {
      setIsPopupOpen(true);
    };
  
    // 팝업 닫기
    const closePopup = () => {
      setIsPopupOpen(false);
    };

    let URL = "";

    const { groupId } = useParams(); // URL 파라미터에서 groupId를 가져옴

    // // 지출 목록과 총액 상태 관리 (서버 연결 테스트용)
    // const [expenses, setExpenses] = useState([
    //     {
    //         expensesId: 1,
    //         expensesDate: "2024-09-24T09:54:22",
    //         itemName: "식비",
    //         amount: 1000,
    //         paidByUserId: "유저1"
    //     },
    //     {
    //         expensesId: 2,
    //         expensesDate: "2024-09-24T11:00:00",
    //         itemName: "교통비",
    //         amount: 3500,
    //         paidByUserId: "유저2"
    //     }
    // ]); // 초기 테스트 데이터

    // 서버 연결을 위한 실제 초기값 설정 (빈 배열)
    const [expenses, setExpenses] = useState([]); // 서버 연결 시 사용
    const [totalAmount, setTotalAmount] = useState(0);

    const url = "";

    useEffect(() => {
      // 서버에서 지출 데이터를 가져오는 함수
      const fetchExpenses = async () => {
          try {
              const response = await axios.get(`/accountBook/${groupId}`); // groupId에 따른 API 요청
              const expensesData = response.data;
              setExpenses(expensesData);
  
              // 서버에서 받은 데이터를 기반으로 총 금액 계산
              const total = expensesData.reduce((acc, expense) => acc + parseFloat(expense.amount), 0);
              setTotalAmount(total);
          } catch (error) {
              console.error("가계부 데이터를 가져오는 중 오류 발생:", error);
          }
      };
  
      fetchExpenses(); // 컴포넌트가 마운트될 때 실행
  }, [groupId]);
  
  // 지출 총액 계산 함수
  useEffect(() => {
      // expenses가 배열일 때만 reduce 호출
      if (Array.isArray(expenses)) {
          const total = expenses.reduce((acc, expense) => acc + parseFloat(expense.amount), 0);
          setTotalAmount(total);
      }
  }, [expenses]); // expenses가 변경될 때마다 totalAmount 업데이트

    const handleAddExpense = (groupId) => {
        // 그룹을 선택하면 /accountBook/{groupId}로 이동
        navigate(`/accountBook/${groupId}/addExpense`);
    };

    const handleItemClick = (expenseId) => {
      navigate(`/expense/${groupId}`, { state: { expenseId: expenseId } }); // groupId는 useParams로 가져온 값을 사용
  };
  

    return (
        <div className="App">
            <div className="container mt-5">
                <div className="row justify-content-center">
                    <div className="col-md-6 text-center">
                        <h1 className="voyage-logo">voyage</h1>
                    </div>
                </div>

                <div className="row justify-content-center mt-4">
                    <div className="col-md-6">
                        <div className="card text-center">
                            <div className="card-body">
                                <h5 className="card-title">지출 총 금액</h5>
                                <h2 className="card-text">{totalAmount.toLocaleString()}원</h2>
                            </div>
                        </div>
                    </div>
                </div>

                <div className="d-flex justify-content-center mt-4">
                    <div className="col-md-6 d-flex justify-content-end" style={{ gap: '20px' }}>
                        <button className="btn" onClick={openPopup}>
                            영수증으로 추가하기 +
                        </button>

                        {/* 팝업을 제어 */}
                        {isPopupOpen && (
                            <AddReceiptPopup onClose={closePopup}/>
                        )}
                        
                        <button className="btn" onClick={() => handleAddExpense(groupId)}>
                            추가하기 +
                        </button>
                    </div>
                </div>

                <div className="card mt-4 text-center">
                    <div className="card-body">
                        {expenses.length > 0 ? (
                            expenses.map((expense) => (
                                <div
                                    className="expense-item"
                                    key={expense.expensesId}
                                    onClick={() => handleItemClick(expense.expensesId)} // 항목 클릭 시 상세보기로 이동
                                    style={{ cursor: 'pointer' }} // 마우스 포인터 추가
                                >
                                    <div className="d-flex justify-content-between align-items-center">
                                        <div>
                                            <p className='expenses-date'>{new Date(expense.expensesDate).toLocaleString()}</p>
                                            <p className="item-name">{expense.itemName}</p>
                                        </div>
                                        <div>
                                            <p>{expense.paidByUserId}</p>
                                            <p className="text-danger">-{parseFloat(expense.amount)}원</p>
                                        </div>
                                    </div>
                                    <hr />
                                </div>
                            ))
                        ) : (
                            <h5>지출을 추가해주세요!</h5>
                        )}
                    </div>
                </div>
            </div>
        </div>
    );
}

export default AccountBook;
