package com.yooyob.erp.exception;

import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;

public class ValidationException extends BusinessException {

    private final Map<String, List<String>> fieldErrors;

    public ValidationException(String message) {
        super(message, "VALIDATION_ERROR", HttpStatus.BAD_REQUEST);
        this.fieldErrors = null;
    }

    public ValidationException(String message, Map<String, List<String>> fieldErrors) {
        super(message, "VALIDATION_ERROR", HttpStatus.BAD_REQUEST);
        this.fieldErrors = fieldErrors;
    }

    public ValidationException(String message, String errorCode) {
        super(message, errorCode, HttpStatus.BAD_REQUEST);
        this.fieldErrors = null;
    }

    public ValidationException(String message, String errorCode, Map<String, List<String>> fieldErrors) {
        super(message, errorCode, HttpStatus.BAD_REQUEST);
        this.fieldErrors = fieldErrors;
    }

    public Map<String, List<String>> getFieldErrors() {
        return fieldErrors;
    }
}