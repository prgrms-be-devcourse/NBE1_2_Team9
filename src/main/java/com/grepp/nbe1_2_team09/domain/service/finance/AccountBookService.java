package com.grepp.nbe1_2_team09.domain.service.finance;

import com.grepp.nbe1_2_team09.common.exception.ExceptionMessage;
import com.grepp.nbe1_2_team09.common.exception.exceptions.AccountBookException;
import com.grepp.nbe1_2_team09.common.exception.exceptions.LocationException;
import com.grepp.nbe1_2_team09.controller.finance.dto.AccountBookReq;
import com.grepp.nbe1_2_team09.controller.finance.dto.AccountBookResp;
import com.grepp.nbe1_2_team09.domain.entity.Expense;
import com.grepp.nbe1_2_team09.domain.entity.Group;
import com.grepp.nbe1_2_team09.domain.entity.GroupMembership;
import com.grepp.nbe1_2_team09.domain.repository.finance.AccountBookRepository;
import com.grepp.nbe1_2_team09.domain.repository.group.GroupRepository;
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

    //가계부 지출 기록
    public void addAccountBook(Long groupId, AccountBookReq accountBookReq) {
        if (accountBookReq.getReceiptImage() != null) {
            byte[] fileData = Base64.getDecoder().decode(accountBookReq.getReceiptImage());
            accountBookReq.setReceiptImageByte(fileData);
        }

        if (accountBookReq.getExpenseDate() == null) {
            accountBookReq.setExpenseDate(LocalDateTime.now());
        }

        Expense expense = AccountBookReq.toEntity(accountBookReq);

        findGroup(groupId);

        Optional<Group> group = groupRepository.findById(groupId);
        expense.setGroup(group.get());
        try {
            accountBookRepository.save(expense);
        } catch (Exception e) {
            throw new AccountBookException(ExceptionMessage.DB_ERROR);
        }
    }

    //가계부 목록 전체 조회
    public List<AccountBookResp> findAllAccountBooks(Long groupId) {
        findGroup(groupId);
        List<Expense> expenses = accountBookRepository.findAllByGroup_GroupId(groupId);

        return expenses.stream()
                .map(AccountBookResp::toDTO)
                .collect(Collectors.toList());
    }

    private void findGroup(Long groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> {
                    log.warn(">>>> {} : {} <<<<", groupId, ExceptionMessage.GROUP_NOT_FOUND);
                    return new AccountBookException(ExceptionMessage.GROUP_NOT_FOUND);
                });
    }
}
