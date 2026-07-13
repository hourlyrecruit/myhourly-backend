package com.my_hourly.exception;

import com.my_hourly.common.enums.ErrorCode;

public class DuplicateResourceException extends BusinessException {

    public DuplicateResourceException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }

}
