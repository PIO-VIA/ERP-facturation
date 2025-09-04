package com.yooyob.erp.exception;

import com.yooyob.erp.dto.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Object>> handleBusinessException(BusinessException ex, WebRequest request) {
        log.error("Erreur métier: {}", ex.getMessage(), ex);

        ApiResponse<Object> response = ApiResponse.<Object>builder()
                .success(false)
                .message(ex.getMessage())
                .errorCode(ex.getErrorCode())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(ex.getHttpStatus()).body(response);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        log.error("Ressource non trouvée: {}", ex.getMessage(), ex);

        ApiResponse<Object> response = ApiResponse.<Object>builder()
                .success(false)
                .message(ex.getMessage())
                .errorCode(ex.getErrorCode())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidationException(ValidationException ex, WebRequest request) {
        log.error("Erreur de validation: {}", ex.getMessage(), ex);

        Map<String, Object> errorDetails = new HashMap<>();
        if (ex.getFieldErrors() != null) {
            errorDetails.put("fieldErrors", ex.getFieldErrors());
        }

        ApiResponse<Object> response = ApiResponse.<Object>builder()
                .success(false)
                .message(ex.getMessage())
                .errorCode(ex.getErrorCode())
                .data(errorDetails)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, WebRequest request) {
        log.error("Validation des arguments échouée: {}", ex.getMessage());

        Map<String, List<String>> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.groupingBy(
                        FieldError::getField,
                        Collectors.mapping(FieldError::getDefaultMessage, Collectors.toList())
                ));

        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("fieldErrors", fieldErrors);

        ApiResponse<Object> response = ApiResponse.<Object>builder()
                .success(false)
                .message("Erreur de validation des données")
                .errorCode("VALIDATION_ERROR")
                .data(errorDetails)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Object>> handleConstraintViolation(ConstraintViolationException ex, WebRequest request) {
        log.error("Violation de contraintes: {}", ex.getMessage());

        Map<String, List<String>> fieldErrors = ex.getConstraintViolations()
                .stream()
                .collect(Collectors.groupingBy(
                        violation -> violation.getPropertyPath().toString(),
                        Collectors.mapping(ConstraintViolation::getMessage, Collectors.toList())
                ));

        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("fieldErrors", fieldErrors);

        ApiResponse<Object> response = ApiResponse.<Object>builder()
                .success(false)
                .message("Erreur de validation des contraintes")
                .errorCode("CONSTRAINT_VIOLATION")
                .data(errorDetails)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(EmailException.class)
    public ResponseEntity<ApiResponse<Object>> handleEmailException(EmailException ex, WebRequest request) {
        log.error("Erreur email: {}", ex.getMessage(), ex);

        ApiResponse<Object> response = ApiResponse.<Object>builder()
                .success(false)
                .message("Erreur lors de l'envoi de l'email: " + ex.getMessage())
                .errorCode(ex.getErrorCode())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(ex.getHttpStatus()).body(response);
    }

    @ExceptionHandler(PdfException.class)
    public ResponseEntity<ApiResponse<Object>> handlePdfException(PdfException ex, WebRequest request) {
        log.error("Erreur PDF: {}", ex.getMessage(), ex);

        ApiResponse<Object> response = ApiResponse.<Object>builder()
                .success(false)
                .message("Erreur lors de la génération du PDF: " + ex.getMessage())
                .errorCode(ex.getErrorCode())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(ex.getHttpStatus()).body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Object>> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        log.error("Argument invalide: {}", ex.getMessage(), ex);

        ApiResponse<Object> response = ApiResponse.<Object>builder()
                .success(false)
                .message("Paramètre invalide: " + ex.getMessage())
                .errorCode("INVALID_ARGUMENT")
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGenericException(Exception ex, WebRequest request) {
        log.error("Erreur interne du serveur: {}", ex.getMessage(), ex);

        ApiResponse<Object> response = ApiResponse.<Object>builder()
                .success(false)
                .message("Une erreur interne s'est produite")
                .errorCode("INTERNAL_SERVER_ERROR")
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Object>> handleRuntimeException(RuntimeException ex, WebRequest request) {
        log.error("Erreur runtime: {}", ex.getMessage(), ex);

        ApiResponse<Object> response = ApiResponse.<Object>builder()
                .success(false)
                .message("Erreur lors du traitement de la requête: " + ex.getMessage())
                .errorCode("RUNTIME_ERROR")
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}