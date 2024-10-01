package com.grepp.nbe1_2_team09.domain.service.finance;

import com.grepp.nbe1_2_team09.common.exception.ExceptionMessage;
import com.grepp.nbe1_2_team09.controller.finance.dto.AccountBookAllResp;
import com.grepp.nbe1_2_team09.controller.finance.dto.AccountBookOneResp;
import com.grepp.nbe1_2_team09.controller.finance.dto.AccountBookReq;
import com.grepp.nbe1_2_team09.controller.finance.dto.UpdateAccountBookReq;
import com.grepp.nbe1_2_team09.domain.entity.Expense;
import com.grepp.nbe1_2_team09.domain.entity.Group;
import com.grepp.nbe1_2_team09.domain.entity.GroupMembership;
import com.grepp.nbe1_2_team09.domain.entity.Role;
import com.grepp.nbe1_2_team09.domain.entity.User;
import com.grepp.nbe1_2_team09.domain.repository.finance.AccountBookRepository;
import com.grepp.nbe1_2_team09.domain.repository.group.GroupMembershipRepository;
import com.grepp.nbe1_2_team09.domain.repository.group.GroupRepository;
import com.grepp.nbe1_2_team09.domain.repository.user.UserRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
    @Autowired
    private GroupMembershipRepository groupMembershipRepository;
    @Autowired
    private UserRepository userRepository;

    @AfterEach
    public void after(){
        accountBookRepository.deleteAll();
        groupRepository.deleteAll();
        userRepository.deleteAll();
        groupMembershipRepository.deleteAll();
    }

    @BeforeEach
    public void before(){
        group = new Group("TestGroup");
        groupResult = groupRepository.save(group);
    }

    @Test
    @DisplayName("지출 목록 삽입에 성공한다")
    public void insert_test() {
        //given
        AccountBookReq req = AccountBookReq.builder()
                .expenseDate(LocalDateTime.now())
                .itemName("food")
                .amount(new BigDecimal(1000).toString())
                .paidByUserId("유저1")
                .build();

        User user=new User("테스트", "aaa@bbb.com", "1234", Role.MEMBER);
        User saveUser = userRepository.save(user);

        GroupMembership groupMembership=new GroupMembership(group, user, Role.MEMBER);
        groupMembershipRepository.save(groupMembership);

        //when
        accountBookService.addAccountBook(groupResult.getGroupId(), req, saveUser.getUserId().toString());

        //then
        Expense expense = accountBookRepository.findAll().get(0);
        Assertions.assertThat(expense.getExpenseDate()).isEqualTo(req.getExpenseDate());
        Assertions.assertThat(expense.getItemName()).isEqualTo(req.getItemName());
        Assertions.assertThat(expense.getPaidBy()).isEqualTo(req.getPaidByUserId());

    }

    @Test
    @DisplayName("그룹을 찾지 못해 삽입에 성공하지 못한다")
    public void not_find_group_test() {
        AccountBookReq req = AccountBookReq.builder()
                .expenseDate(LocalDateTime.now())
                .itemName("food")
                .amount(new BigDecimal(1000).toString())
                .paidByUserId("유저1")
                .build();

        User user=new User("테스트", "aaa@bbb.com", "1234", Role.MEMBER);
        User saveUser = userRepository.save(user);

        try {
            accountBookService.addAccountBook(groupResult.getGroupId() + 12345678, req, saveUser.getUserId().toString());
        } catch (Exception e) {
            org.junit.jupiter.api.Assertions.assertEquals(ExceptionMessage.GROUP_NOT_FOUND.getText(), e.getMessage());
        }
    }

    @Test
    @DisplayName("그룹에 유저가 존재하지 않아 삽입에 실패한다.")
    public void insert_MEMBER_ACCESS_ONLY_test() {
        AccountBookReq req = AccountBookReq.builder()
                .expenseDate(LocalDateTime.now())
                .itemName("food")
                .amount(new BigDecimal(1000).toString())
                .paidByUserId("유저1")
                .build();

        User user=new User("테스트", "aaa@bbb.com", "1234", Role.MEMBER);
        User saveUser = userRepository.save(user);

//        GroupMembership groupMembership=new GroupMembership(group, user, Role.MEMBER);
//        groupMembershipRepository.save(groupMembership);
        //그룹 - 유저를 매핑하지 않음 => 그룹에 유저가 들어가있지 않음

        try {
            accountBookService.addAccountBook(groupResult.getGroupId(), req, saveUser.getUserId().toString());
        } catch (Exception e) {
            org.junit.jupiter.api.Assertions.assertEquals(ExceptionMessage.MEMBER_ACCESS_ONLY.getText(), e.getMessage());
        }
    }

    @Test
    @DisplayName("가계부 목록 전체 조회에 성공한다.")
    void findAllAccountBooks_test() {
        //given
        AccountBookReq req1 = AccountBookReq.builder()
                .expenseDate(LocalDateTime.now())
                .itemName("food")
                .amount(new BigDecimal(1000).toString())
                .paidByUserId("유저1")
                .build();

        AccountBookReq req2 = AccountBookReq.builder()
                .expenseDate(LocalDateTime.now())
                .itemName("food")
                .amount(new BigDecimal("1000.6").toString())
                .paidByUserId("유저2")
                .build();

        AccountBookReq req3 = AccountBookReq.builder()
                .expenseDate(LocalDateTime.now())
                .itemName("food")
                .amount(new BigDecimal("1000.6").toString())
                .paidByUserId("유저2")
                .build();

        User user=new User("테스트", "aaa@bbb.com", "1234", Role.MEMBER);
        User saveUser = userRepository.save(user);

        GroupMembership groupMembership=new GroupMembership(group, user, Role.MEMBER);
        groupMembershipRepository.save(groupMembership);

        accountBookService.addAccountBook(groupResult.getGroupId(), req1, saveUser.getUserId().toString());
        accountBookService.addAccountBook(groupResult.getGroupId(), req2, saveUser.getUserId().toString());
        accountBookService.addAccountBook(groupResult.getGroupId(), req3, saveUser.getUserId().toString());

        //then
        List<AccountBookAllResp> results = accountBookService.findAllAccountBooks(groupResult.getGroupId(), saveUser.getUserId().toString());
        Assertions.assertThat(results.size()).isEqualTo(3);
        Assertions.assertThat(results.get(0).itemName()).isEqualTo("food");
        Assertions.assertThat(results.get(1).itemName()).isEqualTo("food");
    }

    @Test
    @DisplayName("그룹에 유저가 존재하지 않아 가계부 목록 전체 조회에 실패한다.")
    void findAll_MEMBER_ACCESS_ONLY_test() {
        //given
        AccountBookReq req1 = AccountBookReq.builder()
                .expenseDate(LocalDateTime.now())
                .itemName("food")
                .amount(new BigDecimal(1000).toString())
                .paidByUserId("유저1")
                .build();

        AccountBookReq req2 = AccountBookReq.builder()
                .expenseDate(LocalDateTime.now())
                .itemName("food")
                .amount(new BigDecimal("1000.6").toString())
                .paidByUserId("유저2")
                .build();

        AccountBookReq req3 = AccountBookReq.builder()
                .expenseDate(LocalDateTime.now())
                .itemName("food")
                .amount(new BigDecimal("1000.6").toString())
                .paidByUserId("유저2")
                .build();

        User user=new User("테스트", "aaa@bbb.com", "1234", Role.MEMBER);
        User saveUser = userRepository.save(user);

        GroupMembership groupMembership=new GroupMembership(group, user, Role.MEMBER);
        groupMembershipRepository.save(groupMembership); //일단 가계부에 정보를 넣기 위해 유저 하나는 그룹에 넣어놓음

        accountBookService.addAccountBook(groupResult.getGroupId(), req1, saveUser.getUserId().toString());


        User user2=new User("테스트2", "aaa2@bbb.com", "1234", Role.MEMBER);
        User saveUser2 = userRepository.save(user2);

        //then
        try {
            accountBookService.findAllAccountBooks(groupResult.getGroupId(), saveUser2.getUserId().toString());
        } catch (Exception e) {
            org.junit.jupiter.api.Assertions.assertEquals(ExceptionMessage.MEMBER_ACCESS_ONLY.getText(), e.getMessage());
        }
    }

    @Test
    @DisplayName("가계부 목록 상세 조회에 성공한다.")
    void findAccountBook() {
        //given
        String image = Base64.getEncoder().encodeToString("test image".getBytes());

        AccountBookReq req1 = AccountBookReq.builder()
                .expenseDate(LocalDateTime.now())
                .itemName("food")
                .amount(new BigDecimal("1000").toString())
                .paidByUserId("유저1")
                .receiptImage(image)
                .build();

        AccountBookReq req2 = AccountBookReq.builder()
                .expenseDate(LocalDateTime.now())
                .itemName("food")
                .amount(new BigDecimal("1000.6").toString())
                .paidByUserId("유저2")
                .receiptImage(image)
                .build();

        AccountBookReq req3 = AccountBookReq.builder()
                .expenseDate(LocalDateTime.now())
                .itemName("food")
                .amount(new BigDecimal("1000.6").toString())
                .paidByUserId("유저2")
                .receiptImage(image)
                .build();

        User user=new User("테스트", "aaa@bbb.com", "1234", Role.MEMBER);
        User saveUser = userRepository.save(user);

        GroupMembership groupMembership=new GroupMembership(group, user, Role.MEMBER);
        groupMembershipRepository.save(groupMembership);

        accountBookService.addAccountBook(groupResult.getGroupId(), req1, saveUser.getUserId().toString());
        accountBookService.addAccountBook(groupResult.getGroupId(), req2, saveUser.getUserId().toString());
        accountBookService.addAccountBook(groupResult.getGroupId(), req3, saveUser.getUserId().toString());
        List<Expense> all = accountBookRepository.findAll();

        //when
        AccountBookOneResp res1 = accountBookService.findAccountBook(all.get(0).getExpenseId(), saveUser.getUserId().toString());
        AccountBookOneResp res2 = accountBookService.findAccountBook(all.get(1).getExpenseId(), saveUser.getUserId().toString());

        //then
        Assertions.assertThat(res1.getExpensesDate()).isEqualTo(req1.getExpenseDate());
        Assertions.assertThat(res1.getItemName()).isEqualTo(req1.getItemName());
        Assertions.assertThat(new BigDecimal(res1.getAmount()).compareTo(new BigDecimal(req1.getAmount()))).isEqualTo(0);
        Assertions.assertThat(res1.getPaidByUserId()).isEqualTo(req1.getPaidByUserId());
        Assertions.assertThat(res1.getReceiptImage()).isEqualTo(req1.getReceiptImage());
        Assertions.assertThat(res2.getExpensesDate()).isEqualTo(req2.getExpenseDate());
        Assertions.assertThat(res2.getItemName()).isEqualTo(req2.getItemName());
        Assertions.assertThat(new BigDecimal(res2.getAmount()).compareTo(new BigDecimal(req2.getAmount()))).isEqualTo(0);
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
                .amount(new BigDecimal("1000").toString())
                .paidByUserId("유저1")
                .receiptImage(image)
                .build();

        User user=new User("테스트", "aaa@bbb.com", "1234", Role.MEMBER);
        User saveUser = userRepository.save(user);

        GroupMembership groupMembership=new GroupMembership(group, user, Role.MEMBER);
        groupMembershipRepository.save(groupMembership);

        accountBookService.addAccountBook(groupResult.getGroupId(), req1, saveUser.getUserId().toString());

        List<Expense> all = accountBookRepository.findAll();
        AccountBookOneResp res1 = accountBookService.findAccountBook(all.get(0).getExpenseId(), saveUser.getUserId().toString()); //update 전 원본
        Assertions.assertThat(res1.getExpensesDate()).isEqualTo(req1.getExpenseDate());
        Assertions.assertThat(res1.getItemName()).isEqualTo(req1.getItemName());
        Assertions.assertThat(new BigDecimal(res1.getAmount()).compareTo(new BigDecimal(req1.getAmount()))).isEqualTo(0);
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

        accountBookService.updateAccountBook(updateReq, saveUser.getUserId().toString());
        AccountBookOneResp result = accountBookService.findAccountBook(updateReq.getExpenseId(), saveUser.getUserId().toString());

        //then
        Assertions.assertThat(result.getExpensesId()).isEqualTo(res1.getExpensesId()); //update 전/후 아이디가 같은지 확인
        //바뀐게 잘 들어갔는지 확인
        Assertions.assertThat(result.getExpensesDate()).isEqualTo(updateReq.getExpenseDate());
        Assertions.assertThat(result.getItemName()).isEqualTo(updateReq.getItemName());
        Assertions.assertThat(new BigDecimal(result.getAmount()).compareTo(new BigDecimal(updateReq.getAmount().toString()))).isEqualTo(0);
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
                .amount(new BigDecimal(1000).toString())
                .paidByUserId("유저1")
                .build();

        User user=new User("테스트", "aaa@bbb.com", "1234", Role.MEMBER);
        User saveUser = userRepository.save(user);

        GroupMembership groupMembership=new GroupMembership(group, user, Role.MEMBER);
        groupMembershipRepository.save(groupMembership);

        accountBookService.addAccountBook(groupResult.getGroupId(), req1, saveUser.getUserId().toString());

        List<AccountBookAllResp> before = accountBookService.findAllAccountBooks(groupResult.getGroupId(),
                saveUser.getUserId().toString());
        Assertions.assertThat(before.size()).isEqualTo(1);
        Assertions.assertThat(before.get(0).itemName()).isEqualTo("food");

        //when
        accountBookService.deleteAccountBook(before.get(0).expensesId(), saveUser.getUserId().toString());

        //then
        List<AccountBookAllResp> after = accountBookService.findAllAccountBooks(groupResult.getGroupId(),
                saveUser.getUserId().toString());
        Assertions.assertThat(after.size()).isEqualTo(0);
    }
}
