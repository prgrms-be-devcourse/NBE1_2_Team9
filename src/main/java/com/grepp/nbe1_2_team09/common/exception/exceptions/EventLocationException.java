package com.grepp.nbe1_2_team09.common.exception.exceptions;

import com.grepp.nbe1_2_team09.common.exception.BaseException;
import com.grepp.nbe1_2_team09.common.exception.ExceptionMessage;

public class EventLocationException extends BaseException {
    public EventLocationException(ExceptionMessage message) {
        super(message.getText());
    }
}
