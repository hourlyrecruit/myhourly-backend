package com.my_hourly.common.exception;

import com.my_hourly.common.enums.ErrorCode;
import com.my_hourly.common.response.ApiError;

public final class ErrorResponseFactory {

    private ErrorResponseFactory() {
    }

    public static ApiError build(
            String message,
            ErrorCode errorCode,
            String path) {

        return ApiError.builder()
                .success(false)
                .message(message)
                .errorCode(errorCode)
                .path(path)
                .build();
    }

}