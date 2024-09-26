package com.grepp.nbe1_2_team09.domain.service.finance;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.grepp.nbe1_2_team09.common.exception.ExceptionMessage;
import com.grepp.nbe1_2_team09.common.exception.exceptions.AccountBookException;
import com.grepp.nbe1_2_team09.controller.finance.dto.AccountBookReq;
import com.grepp.nbe1_2_team09.domain.entity.Expense;
import com.grepp.nbe1_2_team09.domain.entity.Group;
import com.grepp.nbe1_2_team09.domain.repository.finance.AccountBookRepository;
import com.grepp.nbe1_2_team09.domain.repository.group.GroupRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AccountBookServiceTest {

    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private AccountBookRepository accountBookRepository;
    @Autowired
    private AccountBookService accountBookService;

    @Test
    @DisplayName("지출 목록 삽입에 성공한다")
    public void insert_test(){
        //given
        Group group = new Group("TestGroup");

        AccountBookReq req = AccountBookReq.builder()
                .expenseDate(LocalDateTime.now())
                .itemName("food")
                .amount(new BigDecimal(1000))
                .paidByUserId("유저1")
                .build();

        Group groupResult = groupRepository.save(group);

        //when
        accountBookService.addAccountBook(groupResult.getGroupId(), req);

        //then
        Optional<Expense> expense = accountBookRepository.findById(1L);
        Assertions.assertThat(expense.get().getExpenseDate()).isEqualTo(req.getExpenseDate());
        Assertions.assertThat(expense.get().getItemName()).isEqualTo(req.getItemName());
        Assertions.assertThat(expense.get().getPaidBy()).isEqualTo(req.getPaidByUserId());

    }

    @Test
    @DisplayName("그룹을 찾지 못해 삽입에 성공하지 못한다")
    public void not_find_group_test(){
        Group group = new Group("TestGroup");

        AccountBookReq req = AccountBookReq.builder()
                .expenseDate(LocalDateTime.now())
                .itemName("food")
                .amount(new BigDecimal(1000))
                .paidByUserId("유저1")
                .build();

        Group groupResult = groupRepository.save(group);

        try {
            accountBookService.addAccountBook(groupResult.getGroupId() + 12345678, req);
        }catch (Exception e){
            org.junit.jupiter.api.Assertions.assertEquals(ExceptionMessage.GROUP_NOT_FOUND.getText(), e.getMessage());
        }
    }
}
