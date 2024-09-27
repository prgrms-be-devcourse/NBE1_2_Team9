package com.grepp.nbe1_2_team09.common.exception.exceptions;

import com.grepp.nbe1_2_team09.common.exception.BaseException;
import com.grepp.nbe1_2_team09.common.exception.ExceptionMessage;

public class UserException extends BaseException {
    public UserException(ExceptionMessage message) {
        super(message.getText());
    }
}
