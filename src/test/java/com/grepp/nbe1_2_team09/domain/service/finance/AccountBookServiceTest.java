package com.grepp.nbe1_2_team09.domain.service.finance;

import com.grepp.nbe1_2_team09.common.exception.ExceptionMessage;
import com.grepp.nbe1_2_team09.controller.finance.dto.AccountBookAllResp;
import com.grepp.nbe1_2_team09.controller.finance.dto.AccountBookOneResp;
import com.grepp.nbe1_2_team09.controller.finance.dto.AccountBookReq;
import com.grepp.nbe1_2_team09.controller.finance.dto.UpdateAccountBookReq;
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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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

    Group group;
    Group groupResult;

    @AfterEach
    public void after(){
        accountBookRepository.deleteAll();
        groupRepository.deleteAll();
    }

    @BeforeEach
    public void before(){
        group = new Group("TestGroup");
        groupResult = groupRepository.save(group);
    }

    @Test
    @Order(0)
    @DisplayName("지출 목록 삽입에 성공한다")
    public void insert_test() {
        //given
        AccountBookReq req = AccountBookReq.builder()
                .expenseDate(LocalDateTime.now())
                .itemName("food")
                .amount(new BigDecimal(1000))
                .paidByUserId("유저1")
                .build();

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
        AccountBookReq req = AccountBookReq.builder()
                .expenseDate(LocalDateTime.now())
                .itemName("food")
                .amount(new BigDecimal(1000))
                .paidByUserId("유저1")
                .build();

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

    @Test
    @DisplayName("가계부 지출 수정에 성공한다")
    public void update_test(){
        //given
        String image = Base64.getEncoder().encodeToString("test image".getBytes());
        String updateimage=Base64.getEncoder().encodeToString("update image".getBytes());

        AccountBookReq req1 = AccountBookReq.builder()
                .expenseDate(LocalDateTime.now())
                .itemName("food")
                .amount(new BigDecimal("1000"))
                .paidByUserId("유저1")
                .receiptImage(image)
                .build();

        accountBookService.addAccountBook(groupResult.getGroupId(), req1);

        List<Expense> all = accountBookRepository.findAll();
        AccountBookOneResp res1 = accountBookService.findAccountBook(all.get(0).getExpenseId()); //update 전 원본
        Assertions.assertThat(res1.getExpensesDate()).isEqualTo(req1.getExpenseDate());
        Assertions.assertThat(res1.getItemName()).isEqualTo(req1.getItemName());
        Assertions.assertThat(res1.getAmount().compareTo(req1.getAmount())).isEqualTo(0);
        Assertions.assertThat(res1.getPaidByUserId()).isEqualTo(req1.getPaidByUserId());
        Assertions.assertThat(res1.getReceiptImage()).isEqualTo(req1.getReceiptImage());

        //when
        UpdateAccountBookReq updateReq = UpdateAccountBookReq.builder()
                .expenseId(res1.getExpensesId())
                .expenseDate(LocalDateTime.now())
                .itemName("food")
                .amount(new BigDecimal("1000.6"))
                .paidByUserId("유저2")
                .receiptImage(updateimage)
                .build();

        accountBookService.updateAccountBook(updateReq);
        AccountBookOneResp result = accountBookService.findAccountBook(updateReq.getExpenseId());

        //then
        Assertions.assertThat(result.getExpensesId()).isEqualTo(res1.getExpensesId()); //update 전/후 아이디가 같은지 확인
        //바뀐게 잘 들어갔는지 확인
        Assertions.assertThat(result.getExpensesDate()).isEqualTo(updateReq.getExpenseDate());
        Assertions.assertThat(result.getItemName()).isEqualTo(updateReq.getItemName());
        Assertions.assertThat(result.getAmount().compareTo(updateReq.getAmount())).isEqualTo(0);
        Assertions.assertThat(result.getPaidByUserId()).isEqualTo(updateReq.getPaidByUserId());
        Assertions.assertThat(result.getReceiptImage()).isEqualTo(updateReq.getReceiptImage());
    }

    @Test
    @DisplayName("가계부 지출 삭제에 성공한다.")
    public void delete_test() {
        //given
        AccountBookReq req1 = AccountBookReq.builder()
                .expenseDate(LocalDateTime.now())
                .itemName("food")
                .amount(new BigDecimal(1000))
                .paidByUserId("유저1")
                .build();

        accountBookService.addAccountBook(groupResult.getGroupId(), req1);

        List<AccountBookAllResp> before = accountBookService.findAllAccountBooks(groupResult.getGroupId());
        Assertions.assertThat(before.size()).isEqualTo(1);
        Assertions.assertThat(before.get(0).getItemName()).isEqualTo("food");

        //when
        accountBookService.deleteAccountBook(before.get(0).getExpensesId());

        //then
        List<AccountBookAllResp> after = accountBookService.findAllAccountBooks(groupResult.getGroupId());
        Assertions.assertThat(after.size()).isEqualTo(0);
    }
}
