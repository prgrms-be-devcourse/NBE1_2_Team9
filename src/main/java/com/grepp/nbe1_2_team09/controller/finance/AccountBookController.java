package com.grepp.nbe1_2_team09.controller.finance;

import com.grepp.nbe1_2_team09.common.exception.ExceptionMessage;
import com.grepp.nbe1_2_team09.common.exception.exceptions.AccountBookException;
import com.grepp.nbe1_2_team09.controller.finance.dto.AccountBookReq;
import com.grepp.nbe1_2_team09.domain.entity.Expense;
import com.grepp.nbe1_2_team09.domain.service.finance.AccountBookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@RestController
@RequestMapping("/accountBook")
@RequiredArgsConstructor
public class AccountBookController {

    private final AccountBookService accountBookService;

    @PostMapping("/{groupId}/expense")
    @ResponseStatus(HttpStatus.CREATED)
    public void addAccountBook(@PathVariable Long groupId, @RequestBody AccountBookReq accountBookReq){
        accountBookService.addAccountBook(groupId, accountBookReq);
    }


}
