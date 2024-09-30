package com.grepp.nbe1_2_team09.controller.finance.dto;

import com.grepp.nbe1_2_team09.domain.entity.Expense;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountBookReq {
    private String expenseDate;
    private String itemName;
    private String amount;
    private String paidByUserId;
    private String receiptImage;
    private byte[] receiptImageByte;

    public static Expense toEntity(AccountBookReq accountBookReq){
        Expense expense = new Expense();
        expense.setExpenseDate(LocalDateTime.parse(accountBookReq.getExpenseDate(), DateTimeFormatter.ISO_OFFSET_DATE_TIME));
        expense.setItemName(accountBookReq.getItemName());
        if (accountBookReq.getAmount() != null && !accountBookReq.getAmount().isEmpty()) {
            expense.setAmount(new BigDecimal(accountBookReq.getAmount()));
        } else {
            // amount 값이 null인 경우 = 0
            expense.setAmount(BigDecimal.ZERO);
        }
        expense.setPaidBy(accountBookReq.getPaidByUserId());
        expense.setReceiptImage(accountBookReq.getReceiptImageByte());
        return expense;
    }

    public static AccountBookReq toDTO(Expense expense){
        return AccountBookReq.builder()
                .expenseDate(expense.getExpenseDate().toString())
                .itemName(expense.getItemName())
                .amount(expense.getAmount().toString())
                .paidByUserId(expense.getPaidBy())
                .receiptImageByte(expense.getReceiptImage())
                .build();
    }
}
