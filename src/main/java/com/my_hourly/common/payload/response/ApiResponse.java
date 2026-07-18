package com.my_hourly.common.payload.response;

import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@Builder
@AllArgsConstructor
public class ApiResponse<T> {

    private final boolean success;

    private final String message;

    private final T data;

    @Builder.Default
    private final LocalDateTime timestamp = LocalDateTime.now();

}
