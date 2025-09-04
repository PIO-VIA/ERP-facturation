package com.yooyob.erp.exception;

import org.springframework.http.HttpStatus;

public class PdfException extends BusinessException {

    public PdfException(String message) {
        super(message, "PDF_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public PdfException(String message, Throwable cause) {
        super(message, "PDF_ERROR", HttpStatus.INTERNAL_SERVER_ERROR, cause);
    }

    public PdfException(String message, String errorCode) {
        super(message, errorCode, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public PdfException(String message, String errorCode, Throwable cause) {
        super(message, errorCode, HttpStatus.INTERNAL_SERVER_ERROR, cause);
    }

    public static class PdfGenerationException extends PdfException {
        public PdfGenerationException(String documentType, Throwable cause) {
            super(String.format("Impossible de générer le PDF pour %s", documentType), "PDF_GENERATION_FAILED", cause);
        }
    }

    public static class PdfTemplateException extends PdfException {
        public PdfTemplateException(String templateName, Throwable cause) {
            super(String.format("Erreur lors du traitement du template PDF %s", templateName), "PDF_TEMPLATE_ERROR", cause);
        }
    }

    public static class PdfStorageException extends PdfException {
        public PdfStorageException(String fileName, Throwable cause) {
            super(String.format("Impossible de sauvegarder le fichier PDF %s", fileName), "PDF_STORAGE_ERROR", cause);
        }
    }
}