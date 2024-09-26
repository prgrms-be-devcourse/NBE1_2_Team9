package com.grepp.nbe1_2_team09.controller.finance;

import com.grepp.nbe1_2_team09.controller.finance.dto.AccountBookAllResp;
import com.grepp.nbe1_2_team09.controller.finance.dto.AccountBookOneResp;
import com.grepp.nbe1_2_team09.controller.finance.dto.AccountBookReq;
import com.grepp.nbe1_2_team09.controller.finance.dto.UpdateAccountBookReq;
import com.grepp.nbe1_2_team09.domain.service.finance.AccountBookService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/accountBook")
@RequiredArgsConstructor
public class AccountBookController {

    private final AccountBookService accountBookService;

    //가계부 지출 기록
    @PostMapping("/{groupId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void addAccountBook(@PathVariable Long groupId, @RequestBody AccountBookReq accountBookReq){
        accountBookService.addAccountBook(groupId, accountBookReq);
    }

    //가계부 목록 전체 조회
    @GetMapping("/{groupId}")
    public List<AccountBookAllResp> findAllAccountBooks(@PathVariable Long groupId) {
        return accountBookService.findAllAccountBooks(groupId);
    }

    //가계부 목록 상세 조회
    @GetMapping
    public AccountBookOneResp findAccountBook(@RequestParam Long expenseId) {
        return accountBookService.findAccountBook(expenseId);
    }

    //가계부 지출 기록 수정
    @PutMapping
    public void updateAccountBook(@RequestBody UpdateAccountBookReq updateAccountBookReq){
        accountBookService.updateAccountBook(updateAccountBookReq);
    }
}
