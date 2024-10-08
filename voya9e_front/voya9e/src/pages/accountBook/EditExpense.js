import React, { useState, useEffect } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import axios from 'axios';
import 'bootstrap/dist/css/bootstrap.min.css';
import { useParams } from 'react-router-dom'; // URL 파라미터 가져오기

function EditExpense() {
  const location = useLocation();
  const navigate = useNavigate();
  const { state } = location || {};
  const { groupId } = useParams(); 
  const { expenseId } = state || {};

  const [expenseDetail, setExpenseDetail] = useState({
    expenseDate: "",
    itemName: "",
    amount: "",
    paidByUserId: "",
    receiptImage: null,
  });

  const [receiptFile, setReceiptFile] = useState(null);
  const [errorMessage, setErrorMessage] = useState('');

  const url = "";

  // 수정할 지출 정보 불러오기
  useEffect(() => {
    const fetchExpenseDetail = async () => {
      try {
        const response = await axios.post(url+"/accountBook", {
          expenseId: expenseId
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
      fetchExpenseDetail();
    } else {
      setErrorMessage('지출 항목 ID가 전달되지 않았습니다.');
    }
  }, [expenseId]);

  // 영수증 파일 업로드 핸들러
  const handleReceiptChange = (e) => {
    const file = e.target.files[0];
    setReceiptFile(file);
  };

  // 수정 요청 핸들러
  const handleSave = async () => {
    // 영수증 이미지를 base64로 인코딩
    let receiptImageBase64 = null;
    if (receiptFile) {
      const reader = new FileReader();
      reader.onloadend = () => {
        receiptImageBase64 = reader.result.split(",")[1]; // base64 이미지 데이터만 추출
      };
      reader.readAsDataURL(receiptFile);
    }

    const updatedExpense = {
      expenseId,
      expenseDate: expenseDetail.expenseDate || new Date().toISOString().replace(/\..*/, ""),
      itemName: expenseDetail.itemName,
      amount: expenseDetail.amount,
      paidByUserId: expenseDetail.paidByUserId,
      receiptImage: receiptImageBase64 || expenseDetail.receiptImage, // 새로운 영수증이 없으면 기존 영수증 사용
    };

    try {
      const response = await axios.put(url+`/accountBook`, updatedExpense, {
        headers: {
        //   Authorization: `Bearer YOUR_TOKEN_HERE`, // 실제 토큰으로 교체
        },
      });

      if (response.status === 200) {
        alert("지출 정보가 성공적으로 수정되었습니다.");
        navigate(`/accountBook/${groupId}`); // 수정 후 목록 페이지로 이동
      }
    } catch (error) {
      console.error("지출 정보 수정 중 오류 발생:", error);
      setErrorMessage("지출 정보 수정 중 오류가 발생했습니다.");
    }
  };

  // 삭제 요청 핸들러
  const handleDelete = async () => {
    try {
      const response = await axios.delete(url+`/accountBook`, {
        data:{expenseId},
        headers: {
        //   Authorization: `Bearer YOUR_TOKEN_HERE`, // 실제 토큰으로 교체
        },
      });

      if (response.status === 200) {
        alert("지출 정보가 성공적으로 삭제되었습니다.");
        navigate(`/accountBook/${groupId}`); // 수정 후 목록 페이지로 이동
      }
    } catch (error) {
      console.error("지출 정보 삭제 중 오류 발생:", error);
      setErrorMessage("지출 정보 삭제 중 오류가 발생했습니다.");
    }
  };

  return (
    <div className="container mt-5">
      <h1>지출 수정</h1>

      {errorMessage && <div className="alert alert-danger">{errorMessage}</div>}

      <div className="form-group">
        <label>지출 날짜</label>
        <input
          type="datetime-local"
          className="form-control"
          value={expenseDetail.expenseDate}
          onChange={(e) => setExpenseDetail({ ...expenseDetail, expenseDate: e.target.value })}
        />
      </div>

      <div className="form-group mt-3">
        <label>지출 항목</label>
        <input
          type="text"
          className="form-control"
          value={expenseDetail.itemName}
          onChange={(e) => setExpenseDetail({ ...expenseDetail, itemName: e.target.value })}
        />
      </div>

      <div className="form-group mt-3">
        <label>지출 금액</label>
        <input
          type="number"
          className="form-control"
          value={expenseDetail.amount}
          onChange={(e) => setExpenseDetail({ ...expenseDetail, amount: e.target.value })}
        />
      </div>

      <div className="form-group mt-3">
        <label>지출한 사용자</label>
        <input
          type="text"
          className="form-control"
          value={expenseDetail.paidByUserId}
          onChange={(e) => setExpenseDetail({ ...expenseDetail, paidByUserId: e.target.value })}
        />
      </div>

      <div className="d-flex justify-content-between mt-4">
        <button className="btn btn-cancel" onClick={handleDelete}>삭제</button>
        <button className="btn btn-success" onClick={handleSave}>저장</button>
      </div>
    </div>
  );
}

export default EditExpense;
