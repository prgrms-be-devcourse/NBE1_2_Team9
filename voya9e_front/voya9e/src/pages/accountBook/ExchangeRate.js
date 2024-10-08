import React, { useState } from 'react';
import axios from 'axios';
import 'bootstrap/dist/css/bootstrap.min.css';

function ExchangeRate() {
  const currencyCode = [
    "AED", "AFN", "ALL", "AMD", "ANG", "AOA", "ARS", "AUD", "AWG", "AZN", "BAM",
    "BBD", "BDT", "BGN", "BHD", "BIF", "BMD", "BND", "BOB", "BRL", "BSD", "BTN",
    "BWP", "BYN", "BZD", "CAD", "CDF", "CHF", "CLP", "CNY", "COP", "CRC", "CUP",
    "CVE", "CZK", "DJF", "DKK", "DOP", "DZD", "EGP", "ERN", "ETB", "EUR", "FJD",
    "FKP", "FOK", "GBP", "GEL", "GGP", "GHS", "GIP", "GMD", "GNF", "GTQ", "GYD",
    "HKD", "HNL", "HRK", "HTG", "HUF", "IDR", "ILS", "IMP", "INR", "IQD", "IRR",
    "ISK", "JEP", "JMD", "JOD", "JPY", "KES", "KGS", "KHR", "KID", "KMF", "KRW",
    "KWD", "KYD", "KZT", "LAK", "LBP", "LKR", "LRD", "LSL", "LYD", "MAD", "MDL",
    "MGA", "MKD", "MMK", "MNT", "MOP", "MRU", "MUR", "MVR", "MWK", "MXN", "MYR",
    "MZN", "NAD", "NGN", "NIO", "NOK", "NPR", "NZD", "OMR", "PAB", "PEN", "PGK",
    "PHP", "PKR", "PLN", "PYG", "QAR", "RON", "RSD", "RUB", "RWF", "SAR", "SBD",
    "SCR", "SDG", "SEK", "SGD", "SHP", "SLE", "SOS", "SRD", "SSP", "STN", "SYP",
    "SZL", "THB", "TJS", "TMT", "TND", "TOP", "TRY", "TTD", "TVD", "TWD", "TZS",
    "UAH", "UGX", "USD", "UYU", "UZS", "VES", "VND", "VUV", "WST", "XAF", "XCD",
    "XDR", "XOF", "XPF", "YER", "ZAR", "ZMW", "ZWL"
  ];

  const [toCountry, setToCountry] = useState('');
  const [fromCountry, setFromCountry] = useState('');
  const [amount, setAmount] = useState('');
  const [result, setResult] = useState(null);

  let URL="";

  const handleExchange = async () => {
    if (!toCountry || !fromCountry || !amount) {
      alert('모든 필드를 채워주세요.');
      return;
    }

    try {
      const response = await axios.post(URL+'/exchangeRate', {
        toCountry,
        fromCountry,
        amount,
      });

      if (response.status === 200) {
        setResult(response.data);
      }
    } catch (error) {
      console.error('환전 중 오류 발생:', error);
      alert('환전 중 오류가 발생했습니다. 다시 시도해주세요.');
    }
  };

  return (
    <div className="container mt-5">
      <h1 className="voyage-logo text-center">voyage</h1>
      <div className="card p-4 mt-4">
        <div className="form-group">
          <label>기준 나라</label>
          <select
            className="form-control"
            value={toCountry}
            onChange={(e) => setToCountry(e.target.value)}
          >
            <option value="">선택</option>
            {currencyCode.map((code) => (
              <option key={code} value={code}>
                {code}
              </option>
            ))}
          </select>
        </div>

        <div className="form-group mt-3">
          <label>변환하고 싶은 나라</label>
          <select
            className="form-control"
            value={fromCountry}
            onChange={(e) => setFromCountry(e.target.value)}
          >
            <option value="">선택</option>
            {currencyCode.map((code) => (
              <option key={code} value={code}>
                {code}
              </option>
            ))}
          </select>
        </div>

        <div className="form-group mt-3">
          <label>변환하고 싶은 금액</label>
          <input
            type="number"
            className="form-control"
            value={amount}
            onChange={(e) => setAmount(e.target.value)}
            placeholder="금액을 입력하세요"
          />
        </div>

        <button className="btn btn-success mt-4" onClick={handleExchange}>
          변환
        </button>
      </div>

      {result && (
        <div className="card p-4 mt-4">
          <h5>환율 결과 (기준: {result.time})</h5>
          <p>기준 나라: {result.toCountry}</p>
          <p>변환하고 싶은 나라: {result.fromCountry}</p>
          <p>변환된 금액: {result.fromAmount}</p>
          <p>환율: {result.conversionRate}</p>
        </div>
      )}
    </div>
  );
}

export default ExchangeRate;
