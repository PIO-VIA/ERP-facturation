package com.yooyob.erp.service;

import java.util.Map;
import java.util.UUID;

public interface EmailService {

    /**
     * Envoie un email simple
     */
    void sendSimpleEmail(String to, String subject, String text);

    /**
     * Envoie un email avec template HTML
     */
    void sendHtmlEmail(String to, String subject, String templateName, Map<String, Object> variables);

    /**
     * Envoie un email avec pièce jointe
     */
    void sendEmailWithAttachment(String to, String subject, String text, String attachmentPath, String attachmentName);

    /**
     * Envoie un email HTML avec pièce jointe
     */
    void sendHtmlEmailWithAttachment(String to, String subject, String templateName,
                                     Map<String, Object> variables, String attachmentPath, String attachmentName);

    /**
     * Envoie un email de création de facture
     */
    void sendFactureCreationEmail(UUID factureId, String clientEmail);

    /**
     * Envoie un email de confirmation de paiement
     */
    void sendPaiementConfirmationEmail(UUID paiementId, String clientEmail);

    /**
     * Envoie un email de rappel de paiement
     */
    void sendPaiementReminderEmail(UUID factureId, String clientEmail);

    /**
     * Envoie un email de rappel d'échéance
     */
    void sendEcheanceReminderEmail(UUID factureId, String clientEmail, int joursAvantEcheance);

    /**
     * Envoie un email de facture en retard
     */
    void sendFactureEnRetardEmail(UUID factureId, String clientEmail, int joursDeRetard);

    /**
     * Envoie un email avec le PDF de la facture
     */
    void sendFacturePdfEmail(UUID factureId, String clientEmail, String pdfPath);

    /**
     * Envoie un email de bienvenue à un nouveau client
     */
    void sendWelcomeClientEmail(UUID clientId, String clientEmail);

    /**
     * Envoie un email de notification interne
     */
    void sendInternalNotificationEmail(String subject, String message);

    /**
     * Envoie un rapport mensuel par email
     */
    void sendMonthlyReportEmail(String to, int month, int year);

    /**
     * Teste la configuration email
     */
    void testEmailConfiguration(String testRecipient);

    /**
     * Vérifie si un email est valide
     */
    boolean isValidEmail(String email);

    /**
     * Envoie un email de devis
     */
    void sendDevisEmail(UUID devisId, String clientEmail);

    /**
     * Envoie un email de facture d'avoir
     */
    void sendFactureAvoirEmail(UUID avoirId, String clientEmail);
}