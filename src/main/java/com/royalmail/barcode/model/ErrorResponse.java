package com.royalmail.barcode.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;

/**
 * Standardized error response for all API errors.
 *
 * <p>Provides consistent error information including message, error code,
 * timestamp, and HTTP status for client error handling.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    private final String message;
    private final String errorCode;
    private final LocalDateTime timestamp;
    private final String path;
    private final Integer status;

    public ErrorResponse(String message, String errorCode, Integer status) {
        this.message = message;
        this.errorCode = errorCode;
        this.status = status;
        this.timestamp = LocalDateTime.now();
        this.path = null;
    }

    public ErrorResponse(String message, String errorCode, Integer status, String path) {
        this.message = message;
        this.errorCode = errorCode;
        this.status = status;
        this.path = path;
        this.timestamp = LocalDateTime.now();
    }

    public String getMessage() { return message; }
    public String getErrorCode() { return errorCode; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public String getPath() { return path; }
    public Integer getStatus() { return status; }
}

