package com.my_hourly.common.exception;

import com.my_hourly.common.enums.ErrorCode;
import lombok.Getter;

@Getter
public class BadRequestException extends RuntimeException {

    private final ErrorCode errorCode;

    public BadRequestException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}