package com.grepp.nbe1_2_team09.common.exception;

import com.grepp.nbe1_2_team09.common.exception.exceptions.AccountBookException;
import com.grepp.nbe1_2_team09.common.exception.exceptions.ExchangeRateException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /*
    데이터 바인딩 중 발생하는 에러 BindException 처리
    */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorResponse> bindException(BindException e) {
        String errorMsg = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .collect(Collectors.joining(", "));

        ErrorResponse error = ErrorResponse.from(BAD_REQUEST.value(), BAD_REQUEST.getReasonPhrase(), errorMsg);

        return ResponseEntity.badRequest().body(error);
    }

    /*
        @Valid 어노테이션을 사용한 DTO의 유효성 검사에서 예외가 발생한 경우
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        String errorMsg = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .collect(Collectors.joining(", "));

        ErrorResponse error = ErrorResponse.from(BAD_REQUEST.value(), BAD_REQUEST.getReasonPhrase(), errorMsg);

        return ResponseEntity.badRequest().body(error);
    }

    //클라이언트에 오류 메시지 보내줌. http status는 400
    @ExceptionHandler(ExchangeRateException.class)
    public ResponseEntity<ErrorResponse> handleExchangeRateException(ExchangeRateException e) {
        ErrorResponse error = ErrorResponse.from(BAD_REQUEST.value(), BAD_REQUEST.getReasonPhrase(), e.getMessage());
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(AccountBookException.class)
    public ResponseEntity<ErrorResponse> handleAccountBookException(AccountBookException e) {
        ErrorResponse error = ErrorResponse.from(BAD_REQUEST.value(), BAD_REQUEST.getReasonPhrase(), e.getMessage());
        return ResponseEntity.badRequest().body(error);
    }
}