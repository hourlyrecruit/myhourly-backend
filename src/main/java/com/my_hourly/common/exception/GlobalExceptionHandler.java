package com.my_hourly.common.exception;

import com.my_hourly.common.enums.ErrorCode;
import com.my_hourly.common.payload.response.ApiError;
import com.my_hourly.common.payload.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.ClientAbortException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.async.AsyncRequestNotUsableException;
import tools.jackson.databind.exc.InvalidFormatException;

import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({
            ClientAbortException.class,
            AsyncRequestNotUsableException.class
    })
    public ResponseEntity<Void> handleClientDisconnect(Exception e) {
        if (isClientDisconnect(e)) {
            log.debug("Client disconnected before response could be written: {}", e.getMessage());
        } else {
            log.debug("Client connection aborted: {}", e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<Void> handleClientAbortIOException(IOException e) {
        if (isClientDisconnect(e)) {
            log.debug("Client disconnected during response write: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }
        log.warn("Unhandled IOException during request handling", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

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

        // A client that goes away while the response body is being written surfaces
        // here as a serialization failure (Jackson wraps the aborted socket write in
        // a DatabindException), not as a ClientAbortException. It is not a server
        // fault, so it must not be logged as an unexpected error.
        if (isClientDisconnect(exception)) {
            log.debug("Client disconnected before response could be written: {}", exception.getMessage());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }

        log.error("Unhandled exception while processing {}", request.getRequestURI(), exception);
        String errorMsg = exception.getMessage() != null ? exception.getClass().getSimpleName() + ": " + exception.getMessage() : exception.getClass().getSimpleName();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponseFactory.build(
                        "An unexpected error occurred. (" + errorMsg + ")",
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


    /**
     * True when the exception - or anything in its cause chain - means the client
     * went away while the response was being written (closed tab, aborted fetch,
     * proxy dropping the socket).
     *
     * This cannot be decided by exception type alone: when the abort happens inside
     * the response body serializer, Jackson wraps the failed write in a
     * DatabindException ("ServletOutputStream failed to write: ...") whose cause is
     * an AsyncRequestNotUsableException / ClientAbortException.
     */
    private static boolean isClientDisconnect(Throwable throwable) {
        Throwable current = throwable;
        for (int depth = 0; current != null && depth < 20; depth++) {
            if (current instanceof ClientAbortException
                    || current instanceof AsyncRequestNotUsableException
                    || isClientDisconnectMessage(current.getMessage())) {
                return true;
            }
            Throwable cause = current.getCause();
            current = (cause == current) ? null : cause;
        }
        return false;
    }

    private static boolean isClientDisconnectMessage(String message) {
        if (message == null) {
            return false;
        }
        return message.contains("aborted by the software")
                || message.contains("An established connection was aborted")
                || message.contains("Connection reset")
                || message.contains("Broken pipe")
                || message.contains("ServletOutputStream failed to write");
    }
}