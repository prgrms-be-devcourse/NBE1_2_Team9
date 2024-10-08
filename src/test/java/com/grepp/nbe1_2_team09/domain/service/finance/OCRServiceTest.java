//package com.grepp.nbe1_2_team09.domain.service.finance;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//import com.grepp.nbe1_2_team09.controller.finance.dto.ReceiptDTO;
//import org.assertj.core.api.Assertions;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//@SpringBootTest
//class OCRServiceTest {
//
//    @Autowired
//    private OCRService ocrService;
//
//    @Test
//    @DisplayName("영수증 문자열 포맷팅 테스트")
//    public void StringFormattingTest(){
//        //given
//        ReceiptDTO input1=new ReceiptDTO("거래일시: 2024/09/30 12:46:24", "345,000원");
//        ReceiptDTO input2=new ReceiptDTO("거래일시: 2024년 09월 30일 12:46:24", "총 금액 345.000");
//        ReceiptDTO input3=new ReceiptDTO("2024년 09월 30일 (월) 12시 46분 24초", "최종 결제 금액 345.000");
//
//        //when
//        ReceiptDTO output1=ocrService.ReceiptFormatting(input1);
//        ReceiptDTO output2=ocrService.ReceiptFormatting(input2);
//        ReceiptDTO output3=ocrService.ReceiptFormatting(input3);
//
//        //then
//        Assertions.assertThat(output1.getExpenseDate()).isEqualTo("2024-09-30T12:46:24");
//        Assertions.assertThat(output1.getAmount()).isEqualTo("345000");
//
//        Assertions.assertThat(output2.getExpenseDate()).isEqualTo("2024-09-30T12:46:24");
//        Assertions.assertThat(output2.getAmount()).isEqualTo("345000");
//
//        Assertions.assertThat(output3.getExpenseDate()).isEqualTo("2024-09-30T12:46:24");
//        Assertions.assertThat(output3.getAmount()).isEqualTo("345000");
//
//    }
//
//}
