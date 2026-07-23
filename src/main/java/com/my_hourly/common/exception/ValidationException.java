package com.my_hourly.common.exception;

import com.my_hourly.common.enums.ErrorCode;

public class ValidationException extends BusinessException {

    public ValidationException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }

}
