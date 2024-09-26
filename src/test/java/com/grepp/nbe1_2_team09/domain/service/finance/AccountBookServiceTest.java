package com.grepp.nbe1_2_team09.domain.service.finance;

import com.grepp.nbe1_2_team09.common.exception.ExceptionMessage;
import com.grepp.nbe1_2_team09.controller.finance.dto.AccountBookAllResp;
import com.grepp.nbe1_2_team09.controller.finance.dto.AccountBookOneResp;
import com.grepp.nbe1_2_team09.controller.finance.dto.AccountBookReq;
import com.grepp.nbe1_2_team09.domain.entity.Expense;
import com.grepp.nbe1_2_team09.domain.entity.Group;
import com.grepp.nbe1_2_team09.domain.repository.finance.AccountBookRepository;
import com.grepp.nbe1_2_team09.domain.repository.group.GroupRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
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
    @Order(0)
    @DisplayName("지출 목록 삽입에 성공한다")
    public void insert_test() {
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
    public void not_find_group_test() {
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
        } catch (Exception e) {
            org.junit.jupiter.api.Assertions.assertEquals(ExceptionMessage.GROUP_NOT_FOUND.getText(), e.getMessage());
        }
    }


    @Test
    @DisplayName("가계부 목록 전체 조회에 성공한다.")
    void findAllAccountBooks_test() {
        //given
        Group group = new Group("TestGroup");

        AccountBookReq req1 = AccountBookReq.builder()
                .expenseDate(LocalDateTime.now())
                .itemName("food")
                .amount(new BigDecimal(1000))
                .paidByUserId("유저1")
                .build();

        AccountBookReq req2 = AccountBookReq.builder()
                .expenseDate(LocalDateTime.now())
                .itemName("food")
                .amount(new BigDecimal("1000.6"))
                .paidByUserId("유저2")
                .build();

        AccountBookReq req3 = AccountBookReq.builder()
                .expenseDate(LocalDateTime.now())
                .itemName("food")
                .amount(new BigDecimal("1000.6"))
                .paidByUserId("유저2")
                .build();

        Group groupResult = groupRepository.save(group);
        accountBookService.addAccountBook(groupResult.getGroupId(), req1);
        accountBookService.addAccountBook(groupResult.getGroupId(), req2);
        accountBookService.addAccountBook(groupResult.getGroupId(), req3);

        //then
        List<AccountBookAllResp> results = accountBookService.findAllAccountBooks(groupResult.getGroupId());
        Assertions.assertThat(results.size()).isEqualTo(3);
        Assertions.assertThat(results.get(0).getItemName()).isEqualTo("food");
        Assertions.assertThat(results.get(1).getItemName()).isEqualTo("food");
    }

    @Test
    @DisplayName("가계부 목록 상세 조회에 성공한다.")
    void findAccountBook() {
        //given
        String image = Base64.getEncoder().encodeToString("test image".getBytes());

        Group group = new Group("TestGroup");

        AccountBookReq req1 = AccountBookReq.builder()
                .expenseDate(LocalDateTime.now())
                .itemName("food")
                .amount(new BigDecimal("1000"))
                .paidByUserId("유저1")
                .receiptImage(image)
                .build();

        AccountBookReq req2 = AccountBookReq.builder()
                .expenseDate(LocalDateTime.now())
                .itemName("food")
                .amount(new BigDecimal("1000.6"))
                .paidByUserId("유저2")
                .receiptImage(image)
                .build();

        AccountBookReq req3 = AccountBookReq.builder()
                .expenseDate(LocalDateTime.now())
                .itemName("food")
                .amount(new BigDecimal("1000.6"))
                .paidByUserId("유저2")
                .receiptImage(image)
                .build();

        Group groupResult = groupRepository.save(group);
        accountBookService.addAccountBook(groupResult.getGroupId(), req1);
        accountBookService.addAccountBook(groupResult.getGroupId(), req2);
        accountBookService.addAccountBook(groupResult.getGroupId(), req3);

        //when
        List<Expense> all = accountBookRepository.findAll();
        AccountBookOneResp res1 = accountBookService.findAccountBook(all.get(0).getExpenseId());
        AccountBookOneResp res2 = accountBookService.findAccountBook(all.get(1).getExpenseId());

        //then
        Assertions.assertThat(res1.getExpensesDate()).isEqualTo(req1.getExpenseDate());
        Assertions.assertThat(res1.getItemName()).isEqualTo(req1.getItemName());
        Assertions.assertThat(res1.getAmount().compareTo(req1.getAmount())).isEqualTo(0);
        Assertions.assertThat(res1.getPaidByUserId()).isEqualTo(req1.getPaidByUserId());
        Assertions.assertThat(res1.getReceiptImage()).isEqualTo(req1.getReceiptImage());

        Assertions.assertThat(res2.getExpensesDate()).isEqualTo(req2.getExpenseDate());
        Assertions.assertThat(res2.getItemName()).isEqualTo(req2.getItemName());
        Assertions.assertThat(res2.getAmount().compareTo(req2.getAmount())).isEqualTo(0);
        Assertions.assertThat(res2.getPaidByUserId()).isEqualTo(req2.getPaidByUserId());
        Assertions.assertThat(res2.getReceiptImage()).isEqualTo(req2.getReceiptImage());

    }
}