package com.grepp.nbe1_2_team09.controller.finance.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExchangeRateReqDTO {
    private String toCountry;
    private String fromCountry;
    private BigDecimal Amount;
}
