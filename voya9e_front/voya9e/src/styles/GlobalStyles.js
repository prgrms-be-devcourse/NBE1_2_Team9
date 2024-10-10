import { createGlobalStyle } from 'styled-components';

const GlobalStyles = createGlobalStyle`
  * {
    box-sizing: border-box;
    margin: 0;
    padding: 0;
    font-family: 'Arial', sans-serif;
  }

  .userBody {
    display: flex;
    justify-content: center;
    align-items: center;
    height: 100vh;
    background-color: #f5f5f5;
  }

  // body {
  //   display: flex;
  //   justify-content: center;
  //   align-items: center;
  //   height: 100vh;
  //   background-color: #f5f5f5;
  // }

  .login-container, .signup-container {
    background-color: #ffffff;
    padding: 40px;
    border-radius: 10px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    text-align: center;
    width: 350px;
  }

  h1 {
    font-family: 'Pacifico', cursive;
    font-size: 40px;
    color: #2f4f4f;
    margin-bottom: 30px;
  }

  .userInput {
    width: 100%;
    padding: 12px;
    margin: 10px 0;
    border-radius: 5px;
    border: 1px solid #ccc;
    font-size: 16px;
  }

  .userButton {
    width: 100%;
    padding: 12px;
    border: none;
    border-radius: 5px;
    font-size: 16px;
    cursor: pointer;
  }

  // input {
  //   width: 100%;
  //   padding: 12px;
  //   margin: 10px 0;
  //   border-radius: 5px;
  //   border: 1px solid #ccc;
  //   font-size: 16px;
  // }

  // button {
  //   width: 100%;
  //   padding: 12px;
  //   border: none;
  //   border-radius: 5px;
  //   font-size: 16px;
  //   cursor: pointer;
  // }

  .login-btn {
    background-color: #2e7d32;
    color: white;
    margin-bottom: 20px;
  }

  .kakao-btn {
    width: 100%;
    padding: 12px;
    border: none;
    border-radius: 5px;
    font-size: 16px;
    cursor: pointer;
    background-color: #FEE500;
    color: #3c1e1e;
    font-weight: bold;
    display: flex;
    justify-content: center;
    align-items: center;
  }

  .kakao-btn img {
    margin-right: 8px;
  }

  a {
    display: block;
    margin-top: 20px;
    text-decoration: none;
    color: #555;
  }

  a:hover {
    text-decoration: underline;
  }

  .deleteBtn {
    color: #FF4D4D;
    border: 1px solid #FF4D4D;
  }
  
  .deleteBtn:hover {
    color: #FF3333;
    border-color: #FF3333;
  }
`;

export default GlobalStyles;