package com.grepp.nbe1_2_team09.controller.finance;

import com.grepp.nbe1_2_team09.controller.finance.dto.ExchangeRateReqDTO;
import com.grepp.nbe1_2_team09.controller.finance.dto.ExchangeRateResDTO;
import com.grepp.nbe1_2_team09.domain.service.finance.ExchangeRateService;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/exchangeRate")
@RequiredArgsConstructor
public class ExchangeRateController {

    private final ExchangeRateService exchangeRateService;

    @GetMapping
    public ExchangeRateResDTO exchangeRate(@RequestBody ExchangeRateReqDTO exchangeRateReqDTO){
        return exchangeRateService.exchangeRate(exchangeRateReqDTO);

    }

}
