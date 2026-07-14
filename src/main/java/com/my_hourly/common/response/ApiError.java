package com.my_hourly.common.response;
import com.my_hourly.common.enums.ErrorCode;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ApiError {

    private boolean success;

    private String message;

    private ErrorCode errorCode;

    private String path;

    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

}
