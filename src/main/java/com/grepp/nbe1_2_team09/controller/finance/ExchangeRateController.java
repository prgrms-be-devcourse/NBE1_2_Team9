package com.grepp.nbe1_2_team09.controller.finance;

import com.grepp.nbe1_2_team09.controller.finance.dto.ExchangeRateReqDTO;
import com.grepp.nbe1_2_team09.domain.service.finance.ExchangeRateService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/exchangeRate")
@RequiredArgsConstructor
public class ExchangeRateController {

    private final ExchangeRateService exchangeRateService;

    @GetMapping
    public void exchangeRate(@RequestBody ExchangeRateReqDTO exchangeRateReqDTO){
        exchangeRateService.exchangeRate(exchangeRateReqDTO);
    }

}
