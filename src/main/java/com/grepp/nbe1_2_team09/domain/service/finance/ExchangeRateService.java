package com.grepp.nbe1_2_team09.domain.service.finance;

import com.grepp.nbe1_2_team09.controller.finance.dto.ExchangeRateReqDTO;
import com.grepp.nbe1_2_team09.controller.finance.dto.ExchangeRateResDTO;
import com.grepp.nbe1_2_team09.domain.repository.finance.ExchangeRateRepository;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ExchangeRateService {

    private final ExchangeRateRepository exchangeRateRepository;

    public ExchangeRateResDTO exchangeRate(ExchangeRateReqDTO exchangeRateReqDTO){
        BigDecimal conversionRate=exchangeRateRepository.findConversionRate(exchangeRateReqDTO.getToCountry(), exchangeRateReqDTO.getFromCountry());

        ExchangeRateResDTO result=new ExchangeRateResDTO();
        result.setToCountry(exchangeRateReqDTO.getToCountry());
        result.setFromCountry(exchangeRateReqDTO.getFromCountry());
        result.setToAmount(exchangeRateReqDTO.getAmount());
        result.setConversionRate(conversionRate);
        result.setFromAmount(conversionRate.multiply(exchangeRateReqDTO.getAmount()));

        return result;
    }

}
