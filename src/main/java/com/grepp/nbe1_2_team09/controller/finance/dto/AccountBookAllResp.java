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
import lombok.Setter;
import org.checkerframework.checker.units.qual.A;
import org.springframework.data.convert.Jsr310Converters.StringToLocalDateConverter;

@Builder
public record AccountBookAllResp(
        Long expensesId,
        LocalDateTime expensesDate,
        String itemName,
        String amount,
        String paidByUserId
){
    public static Expense toEntity(AccountBookAllResp accountBookDTO){
        Expense expense = new Expense();

        expense.setExpenseId(accountBookDTO.expensesId());
        expense.setExpenseDate(accountBookDTO.expensesDate());
        expense.setItemName(accountBookDTO.itemName());
        if (accountBookDTO.amount() != null && !accountBookDTO.amount().isEmpty()) {
            expense.setAmount(new BigDecimal(accountBookDTO.amount()));
        } else {
            // amount 값이 null인 경우 = 0
            expense.setAmount(BigDecimal.ZERO);
        }
        expense.setPaidBy(accountBookDTO.paidByUserId());

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
