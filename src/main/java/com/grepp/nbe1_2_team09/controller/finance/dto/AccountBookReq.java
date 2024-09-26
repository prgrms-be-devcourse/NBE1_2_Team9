package com.grepp.nbe1_2_team09.controller.finance.dto;

import com.grepp.nbe1_2_team09.domain.entity.Expense;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountBookReq {
    private LocalDateTime expenseDate;
    private String itemName;
    private BigDecimal amount;
    private String paidByUserId;
    private String receiptImage;
    private byte[] receiptImageByte;

    public static Expense toEntity(AccountBookReq accountBookReq){
        Expense expense = new Expense();
        expense.setExpenseDate(accountBookReq.getExpenseDate());
        expense.setItemName(accountBookReq.getItemName());
        expense.setAmount(accountBookReq.getAmount());
        expense.setPaidBy(accountBookReq.getPaidByUserId());
        expense.setReceiptImage(accountBookReq.getReceiptImageByte());
        return expense;
    }

    public static AccountBookReq toDTO(Expense expense){
        return AccountBookReq.builder()
                .expenseDate(expense.getExpenseDate())
                .itemName(expense.getItemName())
                .amount(expense.getAmount())
                .paidByUserId(expense.getPaidBy())
                .receiptImageByte(expense.getReceiptImage())
                .build();
    }
}
