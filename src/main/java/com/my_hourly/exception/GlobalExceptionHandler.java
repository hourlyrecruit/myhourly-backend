package com.my_hourly.exception;

import com.my_hourly.common.enums.ErrorCode;
import com.my_hourly.common.response.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(
            ResourceNotFoundException exception,
            HttpServletRequest request) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ErrorResponseFactory.build(
                        exception.getMessage(),
                        exception.getErrorCode(),
                        request.getRequestURI()));
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiError> handleValidation(
            ValidationException exception,
            HttpServletRequest request) {

        return ResponseEntity.badRequest()
                .body(ErrorResponseFactory.build(
                        exception.getMessage(),
                        exception.getErrorCode(),
                        request.getRequestURI()));
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiError> handleBusiness(
            BusinessException exception,
            HttpServletRequest request) {

        return ResponseEntity.badRequest()
                .body(ErrorResponseFactory.build(
                        exception.getMessage(),
                        exception.getErrorCode(),
                        request.getRequestURI()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnexpected(
            Exception exception,
            HttpServletRequest request) {

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponseFactory.build(
                        "An unexpected error occurred.",
                        ErrorCode.INTERNAL_SERVER_ERROR,
                        request.getRequestURI()));
    }
}