import React from 'react';
import { useNavigate } from 'react-router-dom';

function TestGroupList() {
  const navigate = useNavigate();

  // 예시 그룹 데이터
  const groups = [
    { id: 1, name: '가족 모임' },
    { id: 2, name: '친구 모임' },
    { id: 3, name: '직장 모임' },
  ];

  // 그룹 선택 핸들러
  const handleSelectGroup = (groupId) => {
    // 그룹을 선택하면 /accountBook/{groupId}로 이동
    navigate(`/accountBook/${groupId}`);
  };
  
  const handelExchangeRate=()=>{
    navigate(`/exchange-rate`);
  }

  const handelChatBot=()=>{
    navigate('/chat');
  }

  return (
    <div>
      <h1>그룹 선택</h1>
      <ul>
        {groups.map(group => (
          <li key={group.id}>
            <button onClick={() => handleSelectGroup(group.id)}>
              {group.name}
            </button>
          </li>
        ))}
      </ul>
      <button onClick={handelExchangeRate}>환율</button>
      <button onClick={handelChatBot}>챗봇</button>
    </div>
  );
}

export default TestGroupList;
