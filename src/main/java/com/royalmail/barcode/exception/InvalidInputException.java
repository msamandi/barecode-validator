package com.royalmail.barcode.exception;

/**
 * Exception thrown when input validation fails (null, blank, etc.).
 */
public class InvalidInputException extends RuntimeException {
    private final String errorCode;
    private final String fieldName;

    public InvalidInputException(String message, String errorCode, String fieldName) {
        super(message);
        this.errorCode = errorCode;
        this.fieldName = fieldName;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getFieldName() {
        return fieldName;
    }
}

