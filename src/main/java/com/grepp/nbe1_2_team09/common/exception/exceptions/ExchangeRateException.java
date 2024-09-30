package com.grepp.nbe1_2_team09.common.exception.exceptions;

import com.grepp.nbe1_2_team09.common.exception.BaseException;
import com.grepp.nbe1_2_team09.common.exception.ExceptionMessage;

public class ExchangeRateException extends BaseException {
    public ExchangeRateException(ExceptionMessage message) {
        super(message.getText());
    }
}
