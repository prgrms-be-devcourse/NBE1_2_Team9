package com.grepp.nbe1_2_team09.common.exception.exceptions;

import com.grepp.nbe1_2_team09.common.exception.BaseException;
import com.grepp.nbe1_2_team09.common.exception.ExceptionMessage;

public class LocationException extends BaseException {
    public LocationException(ExceptionMessage message) {
        super(message.getText());
    }
}
