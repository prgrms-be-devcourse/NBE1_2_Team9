import React, { useState, useEffect, useRef } from 'react';
import axios from 'axios';

const Chat = () => {
    const [message, setMessage] = useState('');
    const [chatHistory, setChatHistory] = useState([]);
    const [isLoading, setIsLoading] = useState(false);
    const chatContainerRef = useRef(null);

    const handleInputChange = (e) => {
        setMessage(e.target.value);
    };

    const handleSendMessage = async () => {
        if (message.trim() === '') return;

        // 사용자 메시지를 채팅 기록에 추가
        setChatHistory([...chatHistory, { role: 'user', content: message }]);
        setMessage(''); // 입력창 초기화
        setIsLoading(true);

        try {
            // 서버로 POST 요청 보내기
            const response = await axios.post('/chat', { message });

            // 서버 응답을 채팅 기록에 추가
            if (response.data) {
                setChatHistory(prevHistory => [
                    ...prevHistory,
                    { role: 'assistant', content: response.data }
                ]);
            }
        } catch (error) {
            console.error('Error sending message:', error);
            setChatHistory(prevHistory => [
                ...prevHistory,
                { role: 'assistant', content: '오류가 발생했습니다. 다시 시도해주세요.' }
            ]);
        } finally {
            setIsLoading(false);
        }
    };

    const handleKeyPress = (e) => {
        if (e.key === 'Enter') {
            handleSendMessage();
        }
    };

    const handleGoBack = () => {
        window.history.back();
    };

    const handleGoHome = () => {
        window.location.href = '/';
    };

    useEffect(() => {
        // 스크롤을 맨 아래로 이동하여 최신 메시지를 보여줌
        if (chatContainerRef.current) {
            chatContainerRef.current.scrollTop = chatContainerRef.current.scrollHeight;
        }
    }, [chatHistory]);

    // 메시지의 길이에 따라 너비를 동적으로 계산하는 함수
    const calculateWidth = (text, maxWidthPercent) => {
        const charWidth = 12; // 글자 하나당 너비
        const maxWidth = window.innerWidth * (maxWidthPercent / 100); // 최대 너비는 화면의 비율로 계산
        const calculatedWidth = Math.min(text.length * charWidth + 20, maxWidth); // 20은 패딩 및 여유 공간
        return `${calculatedWidth}px`;
    };

    return (
        <div style={styles.chatContainer}>
            {/* <div style={styles.header}>
                <button style={styles.backButton} onClick={handleGoBack}> 뒤로가기 </button>
                <div style={styles.voya9e}>voyage</div>
            </div> */}
            <div style={styles.chatHistory} ref={chatContainerRef}>
                {chatHistory.map((chat, index) => (
                    <div
                        key={index}
                        style={{
                            ...styles.message,
                            ...chat.role === 'user'
                                ? { ...styles.userMessage, width: calculateWidth(chat.content, 60) }
                                : { ...styles.botMessage, width: calculateWidth(chat.content, 70) },
                        }}
                    >
                        {chat.content}
                        <br/>
                    </div>
                ))}
                {isLoading && (
                    <div style={styles.loadingMessage}>
                        <div style={styles.loadingBubble}>
                            <span>•</span>
                            <span>•</span>
                            <span>•</span>
                        </div>
                    </div>
                )}
            </div>
            <div style={styles.inputContainer}>
                <input
                    type="text"
                    value={message}
                    onChange={handleInputChange}
                    onKeyPress={handleKeyPress}
                    placeholder="채팅창을 나가면 대화 내역이 사라집니다!"
                    style={styles.input}
                />
                <button onClick={handleSendMessage} style={styles.sendButton}>전송</button>
            </div>
        </div>
    );
};

const styles = {
    chatContainer: {
        display: 'flex',
        position: 'relative',
        flexDirection: 'column',
        height: '91vh',
        // maxWidth: '500px',
        margin: '0 auto',
        border: '1px solid #ccc',
        borderRadius: '8px',
        overflow: 'hidden',
        justifyContent: 'space-between',
    },
    header: {
        display: 'flex',
        justifyContent: 'space-between',
        alignItems: 'center',
        padding: '10px',
        borderBottom: '1px solid #ccc',
        backgroundColor: '#f5f5f5',
    },
    backButton: {
        backgroundColor: 'transparent',
        border: 'none',
        cursor: 'pointer',
        fontSize: '16px',
        color:'black'
    },
    logo: {
        height: '24px',
        cursor: 'pointer',
    },
    chatHistory: {
        flex: 1,
        padding: '10px',
        overflowY: 'auto',
        backgroundColor: '#f5f5f5',
        display: 'flex',
        flexDirection: 'column', // 세로로 배치
    },
    message: {
        margin: '5px 0',
        padding: '10px',
        borderRadius: '10px',
        wordBreak: 'break-word',
        display: 'inline-block', // 각 메시지가 독립적으로 처리되도록 설정
        whiteSpace: 'pre-wrap',
    },
    userMessage: {
        backgroundColor: '#E9E9EB',
        alignSelf: 'flex-end', // 부모 컨테이너의 오른쪽에 배치
        textAlign: 'left',
        maxWidth: '70%', // 너비를 화면의 60%로 제한
    },
    botMessage: {
        backgroundColor: '#fff',
        alignSelf: 'flex-start', // 부모 컨테이너의 왼쪽에 배치
        textAlign: 'left',
        maxWidth: '70%', // 너비를 화면의 70%로 제한
        whiteSpace: 'pre-wrap',
    },
    loadingMessage: {
        display: 'flex',
        justifyContent: 'flex-start',
        margin: '5px 0',
        alignSelf: 'flex-start',
    },
    loadingBubble: {
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        padding: '10px',
        backgroundColor: '#fff',
        borderRadius: '10px',
        maxWidth: '60%',
        animation: 'fade 1.5s infinite',
    },
    loading: {
        display: 'flex',
        justifyContent: 'center',
        alignItems: 'center',
        fontSize: '20px',
    },
    inputContainer: {
        display: 'flex',
        borderTop: '1px solid #ccc',
    },
    input: {
        flex: 1,
        padding: '10px',
        border: 'none',
        borderTopLeftRadius: '0',
        borderTopRightRadius: '0',
    },
    sendButton: {
        padding: '10px',
        backgroundColor: '#008CFF',
        color: 'white',
        border: 'none',
        cursor: 'pointer',
    },
    voya9e: {
        fontFamily: 'Arial, sans-serif',
        fontSize: '1rem',
        fontWeight: 'bold',
        color: '#008CFF',
    },
};


export default Chat;
