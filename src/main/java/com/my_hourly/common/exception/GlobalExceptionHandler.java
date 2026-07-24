package com.my_hourly.common.exception;

import com.my_hourly.common.enums.ErrorCode;
import com.my_hourly.common.payload.response.ApiError;
import com.my_hourly.common.payload.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import tools.jackson.databind.exc.InvalidFormatException;

import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> handleMethodArgumentTypeMismatch(
            MethodArgumentTypeMismatchException exception,
            HttpServletRequest request
    ) {
        String message = String.format("Invalid value '%s' for parameter '%s'", exception.getValue(), exception.getName());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponseFactory.build(
                        message,
                        ErrorCode.INVALID_REQUEST,
                        request.getRequestURI()
                ));
    }

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

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiError> handleBadRequest(
            BadRequestException exception,
            HttpServletRequest request) {

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
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


    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDenied(
            AccessDeniedException exception,
            HttpServletRequest request
    ) {

        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ErrorResponseFactory.build(
                        "Access denied.",
                        ErrorCode.ACCESS_DENIED,
                        request.getRequestURI()
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnexpected(
            Exception exception,
            HttpServletRequest request) {
        exception.printStackTrace(); // Log the unexpected exception details
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponseFactory.build(
                        "An unexpected error occurred.",
                        ErrorCode.INTERNAL_SERVER_ERROR,
                        request.getRequestURI()));
    }


    /**
     * Handles JSON parsing/deserialization errors
     * Example:
     * - Invalid LocalTime format
     * - Invalid LocalDate format
     * - Invalid Enum value
     * - Wrong datatype
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException ex,
            HttpServletRequest request) {

        String message = "Invalid request format";

        Throwable cause = ex.getCause();

        if (cause instanceof InvalidFormatException invalidFormatException) {

            Class<?> targetType = invalidFormatException.getTargetType();

            if (targetType.equals(LocalTime.class)) {
                message = "Invalid time format. Expected format: HH:mm:ss (example: 18:30:00)";
            }
            else if (targetType.equals(LocalDate.class)) {
                message = "Invalid date format. Expected format: yyyy-MM-dd (example: 2026-07-20)";
            }
            else if (targetType.equals(LocalDateTime.class)) {
                message = "Invalid date-time format. Expected format: yyyy-MM-dd HH:mm:ss";
            }
            else {

                if (!invalidFormatException.getPath().isEmpty()) {

                    String fieldName = invalidFormatException
                            .getPath()
                            .getFirst()
                            .getPropertyName();

                    message = "Invalid value for field: " + fieldName;
                }
            }
        }

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponseFactory.build(
                        message,
                        ErrorCode.INVALID_REQUEST,
                        request.getRequestURI()
                ));
    }


    /**
     * Handles Bean Validation errors
     * Example:
     * - @NotNull
     * - @Min
     * - @Size
     * - @Pattern
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .findFirst()
                .orElse("Validation failed");

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponseFactory.build(
                        message,
                        ErrorCode.VALIDATION_FAILED,
                        request.getRequestURI()
                ));
    }
}