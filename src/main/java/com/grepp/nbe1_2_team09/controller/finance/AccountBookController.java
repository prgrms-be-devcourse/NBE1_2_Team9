package com.grepp.nbe1_2_team09.controller.finance;

import com.grepp.nbe1_2_team09.admin.service.CustomUserDetails;
import com.grepp.nbe1_2_team09.controller.finance.dto.AccountBookAllResp;
import com.grepp.nbe1_2_team09.controller.finance.dto.AccountBookOneResp;
import com.grepp.nbe1_2_team09.controller.finance.dto.AccountBookReq;
import com.grepp.nbe1_2_team09.controller.finance.dto.ReceiptDTO;
import com.grepp.nbe1_2_team09.controller.finance.dto.UpdateAccountBookReq;
import com.grepp.nbe1_2_team09.domain.service.finance.AccountBookService;
import com.grepp.nbe1_2_team09.domain.service.finance.OCRService;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/accountBook")
@RequiredArgsConstructor
public class AccountBookController {

    private final AccountBookService accountBookService;
    private final OCRService ocrService;

    //가계부 지출 기록
    @PostMapping("/{groupId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void addAccountBook(@PathVariable Long groupId, @RequestBody AccountBookReq accountBookReq, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        String userId= customUserDetails.getUsername();
        accountBookService.addAccountBook(groupId, accountBookReq, userId);
    }

    //가계부 목록 전체 조회
    @GetMapping("/{groupId}")
    public List<AccountBookAllResp> findAllAccountBooks(@PathVariable Long groupId, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        String userId= customUserDetails.getUsername();
        return accountBookService.findAllAccountBooks(groupId, userId);
    }

    //가계부 목록 상세 조회
    @PostMapping
    public AccountBookOneResp findAccountBook(@RequestBody Map<String, String> expenseId, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        String userId= customUserDetails.getUsername();
        return accountBookService.findAccountBook(Long.parseLong(expenseId.get("expenseId")), userId);
    }

    //가계부 지출 기록 수정
    @PutMapping
    public void updateAccountBook(@RequestBody UpdateAccountBookReq updateAccountBookReq, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        String userId= customUserDetails.getUsername();
        accountBookService.updateAccountBook(updateAccountBookReq, userId);
    }

    //가계부 지출 삭제
    @DeleteMapping
    public void deleteAccountBook(@RequestBody Map<String, String> expenseId, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        String userId= customUserDetails.getUsername();
        accountBookService.deleteAccountBook(Long.parseLong(expenseId.get("expenseId")), userId);
    }

    @PostMapping("/receipt")
    public Map<Integer, String> receiptOCR(@RequestBody Map<String, String> image) throws Exception {
        return ocrService.extractTextFromImage(image.get("image"));
    }

    //받은 문자열 포매팅
    @PostMapping("/receipt/formatting")
    public ReceiptDTO receiptFormatting(@RequestBody ReceiptDTO receipt) {
        return ocrService.formatting(receipt);
    }
}
