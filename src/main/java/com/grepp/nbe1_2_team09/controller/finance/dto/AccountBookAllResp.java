package com.grepp.nbe1_2_team09.controller.finance.dto;

import com.grepp.nbe1_2_team09.domain.entity.Expense;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.convert.Jsr310Converters.StringToLocalDateConverter;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountBookAllResp {

    private Long expensesId;
    private LocalDateTime expensesDate;
    private String itemName;
    private String amount;
    private String paidByUserId;

    public static Expense toEntity(AccountBookAllResp accountBookDTO){
        Expense expense = new Expense();

        expense.setExpenseId(accountBookDTO.getExpensesId());
        expense.setExpenseDate(accountBookDTO.getExpensesDate());
        expense.setItemName(accountBookDTO.getItemName());
        if (accountBookDTO.getAmount() != null && !accountBookDTO.getAmount().isEmpty()) {
            expense.setAmount(new BigDecimal(accountBookDTO.getAmount()));
        } else {
            // amount 값이 null인 경우 = 0
            expense.setAmount(BigDecimal.ZERO);
        }
        expense.setPaidBy(accountBookDTO.getPaidByUserId());

        return expense;
    }

    public static AccountBookAllResp toDTO(Expense expense) {
        return AccountBookAllResp.builder()
                .expensesId(expense.getExpenseId())
                .expensesDate(expense.getExpenseDate())
                .itemName(expense.getItemName())
                .amount(expense.getAmount().toString())
                .paidByUserId(expense.getPaidBy())
                .build();
    }

}
