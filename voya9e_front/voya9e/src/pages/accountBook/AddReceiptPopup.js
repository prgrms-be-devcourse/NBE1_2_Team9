import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import './popup.css'; // 팝업 스타일 정의
import { useParams } from 'react-router-dom'; // URL 파라미터 가져오기
import axios from 'axios';

function AddReceiptPopup({ onClose, onSuccess }) {
  const [selectedFile, setSelectedFile] = useState(null);
  const [errorMessage, setErrorMessage] = useState('');
  const { groupId } = useParams(); 
  const navigate = useNavigate();

  // 파일 선택 핸들러
  const handleFileChange = (e) => {
    setSelectedFile(e.target.files[0]);
  };

  // 파일 제출 핸들러
  const handleSubmit = async () => {
    if (!selectedFile) {
      setErrorMessage("파일을 선택해주세요.");
      return;
    }

    // 파일을 Base64로 인코딩
    const reader = new FileReader();
    reader.onloadend = async () => {
      // 파일 데이터를 읽어와서 Base64 부분만 추출
      const base64String = reader.result.split(',')[1]; // Base64 데이터만 추출

      try {
        // 서버로 POST 요청을 보내기
        const response = await axios.post('/accountBook/receipt', { image: base64String }, {
          headers: {
            'Content-Type': 'application/json'
          },
          timeout: 60000 // 타임아웃 설정
        });

        if (response.status === 200) {
            navigate(`/accountBook/${groupId}/receipt/add`, { state: { data: response.data } }); // 처리된 데이터를 새로운 페이지로 전달
            // 성공적으로 처리된 데이터를 콜백 함수에 전달
          // await onSuccess(response.data);
          onSuccess(base64String);
          // 팝업을 닫음
          onClose();
        }
      } catch (error) {
        // 오류 처리
        setErrorMessage("파일 처리 중 오류가 발생했습니다.");
        console.error("Error:", error);
      }
    };

    reader.onerror = () => {
      setErrorMessage("파일을 읽는 중 오류가 발생했습니다.");
    };

    reader.readAsDataURL(selectedFile); // Base64로 파일을 읽기
  };

  return (
    <div className="popup-overlay">
      <div className="popup-content">
        <h2>영수증 추가</h2>
        {errorMessage && <p className="error-message">{errorMessage}</p>}
        
        <input type="file" accept="image/*" onChange={handleFileChange} />
        <div className="popup-buttons">
          <button className="btn btn-success" onClick={handleSubmit}>추가하기</button>
          <button className="btn btn-secondary btn-cancel" onClick={onClose}>취소</button>
        </div>
      </div>
    </div>
  );
}

export default AddReceiptPopup;
