package com.my_hourly.common.exception;

import com.my_hourly.common.enums.ErrorCode;

public class PanMismatchException
        extends RuntimeException {

    private final ErrorCode errorCode;

    public PanMismatchException(
            String message,
            ErrorCode errorCode
    ) {
        super(message);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}