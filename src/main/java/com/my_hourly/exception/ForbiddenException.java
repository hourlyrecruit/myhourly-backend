package com.my_hourly.exception;

import com.my_hourly.common.enums.ErrorCode;

public class ForbiddenException extends BusinessException {

    public ForbiddenException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }

}