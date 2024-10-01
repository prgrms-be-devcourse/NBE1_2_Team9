package com.grepp.nbe1_2_team09.controller.finance.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExchangeRateResDTO {
    private LocalDateTime time;
    private String toCountry;
    private String fromCountry;
    private BigDecimal toAmount;
    private BigDecimal conversionRate;
    private BigDecimal fromAmount;
}
