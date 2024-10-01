package com.grepp.nbe1_2_team09.domain.service.finance;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grepp.nbe1_2_team09.common.exception.ExceptionMessage;
import com.grepp.nbe1_2_team09.common.exception.exceptions.ExchangeRateException;
import com.grepp.nbe1_2_team09.controller.finance.dto.ExchangeRateReqDTO;
import com.grepp.nbe1_2_team09.controller.finance.dto.ExchangeRateResDTO;
import com.grepp.nbe1_2_team09.domain.entity.ExchangeRate;
import com.grepp.nbe1_2_team09.domain.repository.finance.ExchangeRateRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
@Component
public class ExchangeRateService {

    private final ExchangeRateRepository exchangeRateRepository;
    @Value("${exchangeRate-API_KEY}")
    private String exchangeRateApiKey;
    private final EntityManager em;
    private static LocalDateTime exchangeRateTime = LocalDateTime.now();

    private final String[] currencyCode={"AED", "AFN", "ALL", "AMD", "ANG", "AOA", "ARS", "AUD", "AWG", "AZN", "BAM", "BBD", "BDT", "BGN", "BHD", "BIF", "BMD", "BND", "BOB", "BRL", "BSD", "BTN", "BWP", "BYN", "BZD", "CAD", "CDF", "CHF", "CLP", "CNY", "COP", "CRC", "CUP", "CVE", "CZK", "DJF", "DKK", "DOP", "DZD", "EGP", "ERN", "ETB", "EUR", "FJD", "FKP", "FOK", "GBP", "GEL", "GGP", "GHS", "GIP", "GMD", "GNF", "GTQ", "GYD", "HKD", "HNL", "HRK", "HTG", "HUF", "IDR", "ILS", "IMP", "INR", "IQD", "IRR", "ISK", "JEP", "JMD", "JOD", "JPY", "KES", "KGS", "KHR", "KID", "KMF", "KRW", "KWD", "KYD", "KZT", "LAK", "LBP", "LKR", "LRD", "LSL", "LYD", "MAD", "MDL", "MGA", "MKD", "MMK", "MNT", "MOP", "MRU", "MUR", "MVR", "MWK", "MXN", "MYR", "MZN", "NAD", "NGN", "NIO", "NOK", "NPR", "NZD", "OMR", "PAB", "PEN", "PGK", "PHP", "PKR", "PLN", "PYG", "QAR", "RON", "RSD", "RUB", "RWF", "SAR", "SBD", "SCR", "SDG", "SEK", "SGD", "SHP", "SLE", "SOS", "SRD", "SSP", "STN", "SYP", "SZL", "THB", "TJS", "TMT", "TND", "TOP", "TRY", "TTD", "TVD", "TWD", "TZS", "UAH", "UGX", "USD", "UYU", "UZS", "VES", "VND", "VUV", "WST", "XAF", "XCD", "XDR", "XOF", "XPF", "YER", "ZAR", "ZMW", "ZWL"};

    public ExchangeRateResDTO exchangeRate(ExchangeRateReqDTO exchangeRateReqDTO){
        BigDecimal conversionRate=exchangeRateRepository.findConversionRate(exchangeRateReqDTO.getToCountry(), exchangeRateReqDTO.getFromCountry());

        ExchangeRateResDTO result=new ExchangeRateResDTO();
        result.setTime(exchangeRateTime);
        result.setToCountry(exchangeRateReqDTO.getToCountry());
        result.setFromCountry(exchangeRateReqDTO.getFromCountry());
        result.setToAmount(new BigDecimal(exchangeRateReqDTO.getAmount()));
        result.setConversionRate(conversionRate);
        result.setFromAmount(conversionRate.multiply(new BigDecimal(exchangeRateReqDTO.getAmount())));

        return result;
    }

    //특정 시간마다 환율 정보 갱신
    @Transactional
    @Scheduled(cron = "0 0 0 * * *") //매일 0시에 실행
    public void exchangeRateSchedule(){
        exchangeRateTime=LocalDateTime.now();
        int size=currencyCode.length;

        for(int i=0;i<size;i++){
            String urlStr="https://v6.exchangerate-api.com/v6/"+exchangeRateApiKey+"/latest/"+currencyCode[i]; //toCountry
            try {
                URL url = new URL(urlStr);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Accept", "application/json");

                if (conn.getResponseCode() != 200) {
                    throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
                }

                BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
                StringBuilder responseBuilder = new StringBuilder();
                String line;

                while ((line = br.readLine()) != null) {
                    responseBuilder.append(line);
                }

                br.close();
                String response = responseBuilder.toString();

                conn.disconnect();

                JSONObject json = new JSONObject(response);
                JSONObject rates = json.getJSONObject("conversion_rates");
                Map<String, Object> map=null;
                try{
                    map=new ObjectMapper().readValue(rates.toString(), Map.class);
                }catch (Exception e){
                    e.printStackTrace();
                }

                for (int j=0;j<currencyCode.length;j++) {
                    String rate = map.get(currencyCode[j]).toString();
                    Long id = exchangeRateRepository.findIdByFromCountryAndToCountry(currencyCode[j], currencyCode[i]);
                    ExchangeRate exchangeRate = em.find(ExchangeRate.class, id); //id 찾아서 em에 넣음
                    if (exchangeRate != null) {
                        exchangeRate.setConversionRate(new BigDecimal(rate)); //환율 업데이트
                        em.persist(exchangeRate);
                    }
                }

            }catch (Exception e){
                e.printStackTrace();
                log.warn(">>>> {} : {} <<<<", e, new ExchangeRateException(ExceptionMessage.EXCHANGE_ERROR));
                throw new ExchangeRateException(ExceptionMessage.EXCHANGE_ERROR);
            }
        }
    }
}
