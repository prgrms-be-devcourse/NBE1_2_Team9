package com.grepp.nbe1_2_team09.controller.finance.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReceiptDTO {
    private String expenseDate;
    private String amount;
}
