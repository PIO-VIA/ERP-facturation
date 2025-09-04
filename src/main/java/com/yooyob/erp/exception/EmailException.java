package com.yooyob.erp.exception;

import org.springframework.http.HttpStatus;

public class EmailException extends BusinessException {

    public EmailException(String message) {
        super(message, "EMAIL_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public EmailException(String message, Throwable cause) {
        super(message, "EMAIL_ERROR", HttpStatus.INTERNAL_SERVER_ERROR, cause);
    }

    public EmailException(String message, String errorCode) {
        super(message, errorCode, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public EmailException(String message, String errorCode, Throwable cause) {
        super(message, errorCode, HttpStatus.INTERNAL_SERVER_ERROR, cause);
    }

    public static class EmailSendException extends EmailException {
        public EmailSendException(String recipient, Throwable cause) {
            super(String.format("Impossible d'envoyer l'email à %s", recipient), "EMAIL_SEND_FAILED", cause);
        }
    }

    public static class EmailTemplateException extends EmailException {
        public EmailTemplateException(String templateName, Throwable cause) {
            super(String.format("Erreur lors du traitement du template email %s", templateName), "EMAIL_TEMPLATE_ERROR", cause);
        }
    }

    public static class EmailConfigurationException extends EmailException {
        public EmailConfigurationException(String message) {
            super(message, "EMAIL_CONFIGURATION_ERROR");
        }
    }
}