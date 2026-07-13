package com.my_hourly.exception;

import com.my_hourly.common.enums.ErrorCode;

public class ResourceNotFoundException extends BusinessException {

    public ResourceNotFoundException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }

}
