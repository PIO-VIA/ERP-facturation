package com.yooyob.erp.exception;

import org.springframework.http.HttpStatus;

import java.util.UUID;

public class ResourceNotFoundException extends BusinessException {

    public ResourceNotFoundException(String resourceName, UUID id) {
        super(
                String.format("%s avec l'ID %s n'a pas été trouvé", resourceName, id),
                "RESOURCE_NOT_FOUND",
                HttpStatus.NOT_FOUND
        );
    }

    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(
                String.format("%s avec %s '%s' n'a pas été trouvé", resourceName, fieldName, fieldValue),
                "RESOURCE_NOT_FOUND",
                HttpStatus.NOT_FOUND
        );
    }

    public ResourceNotFoundException(String message) {
        super(message, "RESOURCE_NOT_FOUND", HttpStatus.NOT_FOUND);
    }

    public ResourceNotFoundException(String message, String errorCode) {
        super(message, errorCode, HttpStatus.NOT_FOUND);
    }
}