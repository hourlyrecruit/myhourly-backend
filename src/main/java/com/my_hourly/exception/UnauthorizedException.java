package com.my_hourly.exception;

import com.my_hourly.common.enums.ErrorCode;

public class UnauthorizedException extends BusinessException {

    public UnauthorizedException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }

}
