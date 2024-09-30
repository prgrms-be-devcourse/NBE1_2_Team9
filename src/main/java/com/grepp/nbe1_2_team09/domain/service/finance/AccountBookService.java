package com.grepp.nbe1_2_team09.domain.service.finance;

import com.grepp.nbe1_2_team09.common.exception.ExceptionMessage;
import com.grepp.nbe1_2_team09.common.exception.exceptions.AccountBookException;
import com.grepp.nbe1_2_team09.controller.finance.dto.AccountBookAllResp;
import com.grepp.nbe1_2_team09.controller.finance.dto.AccountBookOneResp;
import com.grepp.nbe1_2_team09.controller.finance.dto.AccountBookReq;
import com.grepp.nbe1_2_team09.controller.finance.dto.UpdateAccountBookReq;
import com.grepp.nbe1_2_team09.domain.entity.Expense;
import com.grepp.nbe1_2_team09.domain.entity.Group;
import com.grepp.nbe1_2_team09.domain.entity.User;
import com.grepp.nbe1_2_team09.domain.repository.finance.AccountBookRepository;
import com.grepp.nbe1_2_team09.domain.repository.group.GroupMembershipRepository;
import com.grepp.nbe1_2_team09.domain.repository.group.GroupRepository;
import com.grepp.nbe1_2_team09.domain.repository.user.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountBookService {

    private final AccountBookRepository accountBookRepository;
    private final GroupRepository groupRepository;
    private final GroupMembershipRepository groupMembershipRepository;
    private final EntityManager em;
    private final UserRepository userRepository;

    //가계부 지출 기록
    public void addAccountBook(Long groupId, AccountBookReq accountBookReq, String user) {
        if (accountBookReq.getReceiptImage() != null) {
            byte[] fileData = Base64.getDecoder().decode(accountBookReq.getReceiptImage());
            accountBookReq.setReceiptImageByte(fileData);
        }

        if (accountBookReq.getExpenseDate() == null) {
            accountBookReq.setExpenseDate(LocalDateTime.now().toString());
        }

        Expense expense = AccountBookReq.toEntity(accountBookReq);

        Long userId=Long.parseLong(user);
        checkUserInGroup(groupId, userId);

        Optional<Group> group = groupRepository.findById(groupId);
        expense.setGroup(group.get());
        try {
            accountBookRepository.save(expense);
        } catch (Exception e) {
            throw new AccountBookException(ExceptionMessage.DB_ERROR);
        }
    }

    //가계부 목록 전체 조회
    public List<AccountBookAllResp> findAllAccountBooks(Long groupId, String user) {
        Long userId=Long.parseLong(user);
        checkUserInGroup(groupId, userId);

        List<Expense> expenses = accountBookRepository.findAllByGroup_GroupId(groupId);

        return expenses.stream()
                .map(AccountBookAllResp::toDTO)
                .collect(Collectors.toList());
    }

    //가계부 목록 상세 조회
    @Transactional
    public AccountBookOneResp findAccountBook(Long expenseId, String user) {
        Expense expense = accountBookRepository.findById(expenseId)
                .orElseThrow(()->{
                    log.warn(">>>> {} : {} <<<<", expenseId, ExceptionMessage.EXPENSE_NOT_FOUND);
                    return new AccountBookException(ExceptionMessage.EXPENSE_NOT_FOUND);
                });

        Long groupId=accountBookRepository.findById(expenseId).get().getGroup().getGroupId();
        Long userId=Long.parseLong(user);
        checkUserInGroup(groupId, userId);

        AccountBookOneResp accountBookOneResp = AccountBookOneResp.toDTO(expense);

        String image=null;
        if(expense.getReceiptImage()!=null){
            image=Base64.getEncoder().encodeToString(expense.getReceiptImage());
        }
        accountBookOneResp.setReceiptImage(image);
        return accountBookOneResp;
    }

    //가계부 지출 수정
    @Transactional
    public void updateAccountBook(UpdateAccountBookReq updateAccountBookReq, String user) {
        try{
            accountBookRepository.findById(updateAccountBookReq.getExpenseId());
        } catch(Exception e){
            throw new AccountBookException(ExceptionMessage.EXPENSE_NOT_FOUND);
        }

        Long groupId=accountBookRepository.findById(updateAccountBookReq.getExpenseId()).get().getGroup().getGroupId();
        Long userId=Long.parseLong(user);
        checkUserInGroup(groupId, userId);

        Expense expense=em.find(Expense.class, updateAccountBookReq.getExpenseId());

        expense.setExpenseDate(updateAccountBookReq.getExpenseDate());
        expense.setItemName(updateAccountBookReq.getItemName());
        expense.setAmount(updateAccountBookReq.getAmount());
        expense.setPaidBy(updateAccountBookReq.getPaidByUserId());

        if (updateAccountBookReq.getReceiptImage() != null) {
            byte[] fileData = Base64.getDecoder().decode(updateAccountBookReq.getReceiptImage());
            updateAccountBookReq.setReceiptImageByte(fileData);
        }
        expense.setReceiptImage(updateAccountBookReq.getReceiptImageByte());
    }

    //가계부 지출 삭제
    @Transactional
    public void deleteAccountBook(Long expenseId, String user) {
        try{
            accountBookRepository.findById(expenseId);
        } catch(Exception e){
            throw new AccountBookException(ExceptionMessage.EXPENSE_NOT_FOUND);
        }

        Long groupId=accountBookRepository.findById(expenseId).get().getGroup().getGroupId();
        Long userId=Long.parseLong(user);
        checkUserInGroup(groupId, userId);

        accountBookRepository.deleteById(expenseId);
    }

    private void checkUserInGroup(Long groupId, Long userId){
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> {
                    log.warn(">>>> {} : {} <<<<", groupId, new AccountBookException(ExceptionMessage.GROUP_NOT_FOUND));
                    return new AccountBookException(ExceptionMessage.GROUP_NOT_FOUND);
                });

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn(">>>> {} : {} <<<<", userId, new AccountBookException(ExceptionMessage.USER_NOT_FOUND));
                    return new AccountBookException(ExceptionMessage.USER_NOT_FOUND);
                });

        boolean isMember=groupMembershipRepository.existsByGroupAndUser(group, user);
        if(!isMember){
            log.warn(">>>> {} : {} <<<<", userId, new AccountBookException(ExceptionMessage.MEMBER_ACCESS_ONLY));
            throw new AccountBookException(ExceptionMessage.MEMBER_ACCESS_ONLY);
        }
    }
}
