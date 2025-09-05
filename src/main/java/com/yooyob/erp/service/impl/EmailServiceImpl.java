package com.yooyob.erp.service.impl;

import com.yooyob.erp.exception.EmailException;
import com.yooyob.erp.service.EmailService;
import com.yooyob.erp.service.FactureService;
import com.yooyob.erp.service.PaiementService;
import com.yooyob.erp.service.ClientService;
import com.yooyob.erp.dto.response.FactureResponse;
import com.yooyob.erp.dto.response.PaiementResponse;
import com.yooyob.erp.dto.response.ClientResponse;
import com.yooyob.erp.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;
    private final FactureService factureService;
    private final PaiementService paiementService;
    private final ClientService clientService;

    public EmailServiceImpl(JavaMailSender mailSender, SpringTemplateEngine templateEngine, 
                           @Lazy FactureService factureService, PaiementService paiementService, 
                           ClientService clientService) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
        this.factureService = factureService;
        this.paiementService = paiementService;
        this.clientService = clientService;
    }

    @Value("${app.email.from}")
    private String fromEmail;

    @Value("${app.email.templates.base-url}")
    private String baseUrl;

    private static final String CHARSET_UTF8 = "UTF-8";

    @Override
    public void sendSimpleEmail(String to, String subject, String text) {
        log.info("Envoi d'un email simple à: {}", to);

        validateEmailParameters(to, subject);

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            message.setSentDate(new java.util.Date());

            mailSender.send(message);
            log.info("Email simple envoyé avec succès à: {}", to);

        } catch (Exception e) {
            log.error("Erreur lors de l'envoi de l'email simple à {}: {}", to, e.getMessage(), e);
            throw new EmailException.EmailSendException(to, e);
        }
    }

    @Override
    public void sendHtmlEmail(String to, String subject, String templateName, Map<String, Object> variables) {
        log.info("Envoi d'un email HTML avec template {} à: {}", templateName, to);

        validateEmailParameters(to, subject);
        validateTemplate(templateName);

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, CHARSET_UTF8);

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);

            String htmlContent = processTemplate(templateName, variables);
            helper.setText(htmlContent, true);

            mailSender.send(mimeMessage);
            log.info("Email HTML envoyé avec succès à: {}", to);

        } catch (MessagingException e) {
            log.error("Erreur de messagerie lors de l'envoi de l'email HTML à {}: {}", to, e.getMessage(), e);
            throw new EmailException.EmailSendException(to, e);
        } catch (Exception e) {
            log.error("Erreur lors de l'envoi de l'email HTML à {}: {}", to, e.getMessage(), e);
            throw new EmailException.EmailSendException(to, e);
        }
    }

    @Override
    public void sendEmailWithAttachment(String to, String subject, String text, String attachmentPath, String attachmentName) {
        log.info("Envoi d'un email avec pièce jointe à: {}", to);

        validateEmailParameters(to, subject);
        validateAttachment(attachmentPath, attachmentName);

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, CHARSET_UTF8);

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text);

            FileSystemResource file = new FileSystemResource(new File(attachmentPath));
            helper.addAttachment(attachmentName, file);

            mailSender.send(mimeMessage);
            log.info("Email avec pièce jointe envoyé avec succès à: {}", to);

        } catch (MessagingException e) {
            log.error("Erreur de messagerie lors de l'envoi de l'email avec pièce jointe à {}: {}", to, e.getMessage(), e);
            throw new EmailException.EmailSendException(to, e);
        } catch (Exception e) {
            log.error("Erreur lors de l'envoi de l'email avec pièce jointe à {}: {}", to, e.getMessage(), e);
            throw new EmailException.EmailSendException(to, e);
        }
    }

    @Override
    public void sendHtmlEmailWithAttachment(String to, String subject, String templateName,
                                            Map<String, Object> variables, String attachmentPath, String attachmentName) {
        log.info("Envoi d'un email HTML avec pièce jointe à: {}", to);

        validateEmailParameters(to, subject);
        validateTemplate(templateName);
        validateAttachment(attachmentPath, attachmentName);

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, CHARSET_UTF8);

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);

            String htmlContent = processTemplate(templateName, variables);
            helper.setText(htmlContent, true);

            FileSystemResource file = new FileSystemResource(new File(attachmentPath));
            helper.addAttachment(attachmentName, file);

            mailSender.send(mimeMessage);
            log.info("Email HTML avec pièce jointe envoyé avec succès à: {}", to);

        } catch (MessagingException e) {
            log.error("Erreur de messagerie lors de l'envoi de l'email HTML avec pièce jointe à {}: {}", to, e.getMessage(), e);
            throw new EmailException.EmailSendException(to, e);
        } catch (Exception e) {
            log.error("Erreur lors de l'envoi de l'email HTML avec pièce jointe à {}: {}", to, e.getMessage(), e);
            throw new EmailException.EmailSendException(to, e);
        }
    }

    @Override
    public void sendFactureCreationEmail(UUID factureId, String clientEmail) {
        log.info("Envoi d'email de création de facture {} à: {}", factureId, clientEmail);

        FactureResponse facture = factureService.getFactureById(factureId);

        Map<String, Object> variables = new HashMap<>();
        variables.put("facture", facture);
        variables.put("client", facture.getNomClient());
        variables.put("numeroFacture", facture.getNumeroFacture());
        variables.put("montantTotal", facture.getMontantTotal());
        variables.put("dateFacturation", facture.getDateFacturation());
        variables.put("dateEcheance", facture.getDateEcheance());
        variables.put("baseUrl", baseUrl);

        String subject = String.format("Nouvelle facture %s - Montant: %s",
                facture.getNumeroFacture(),
                facture.getMontantTotal());

        sendHtmlEmail(clientEmail, subject, "facture-creation", variables);
    }

    @Override
    public void sendPaiementConfirmationEmail(UUID paiementId, String clientEmail) {
        log.info("Envoi d'email de confirmation de paiement {} à: {}", paiementId, clientEmail);

        PaiementResponse paiement = paiementService.getPaiementById(paiementId);

        Map<String, Object> variables = new HashMap<>();
        variables.put("paiement", paiement);
        variables.put("montant", paiement.getMontant());
        variables.put("datePaiement", paiement.getDate());
        variables.put("modePaiement", paiement.getModePaiement().getLibelle());
        variables.put("baseUrl", baseUrl);

        if (paiement.getIdFacture() != null) {
            FactureResponse facture = factureService.getFactureById(paiement.getIdFacture());
            variables.put("facture", facture);
        }

        String subject = String.format("Confirmation de paiement - Montant: %s", paiement.getMontant());

        sendHtmlEmail(clientEmail, subject, "paiement-recu", variables);
    }

    @Override
    public void sendPaiementReminderEmail(UUID factureId, String clientEmail) {
        log.info("Envoi d'email de rappel de paiement pour facture {} à: {}", factureId, clientEmail);

        FactureResponse facture = factureService.getFactureById(factureId);

        Map<String, Object> variables = new HashMap<>();
        variables.put("facture", facture);
        variables.put("numeroFacture", facture.getNumeroFacture());
        variables.put("montantRestant", facture.getMontantRestant());
        variables.put("dateEcheance", facture.getDateEcheance());
        variables.put("client", facture.getNomClient());
        variables.put("baseUrl", baseUrl);

        String subject = String.format("Rappel de paiement - Facture %s", facture.getNumeroFacture());

        sendHtmlEmail(clientEmail, subject, "rappel-paiement", variables);
    }

    @Override
    public void sendEcheanceReminderEmail(UUID factureId, String clientEmail, int joursAvantEcheance) {
        log.info("Envoi d'email de rappel d'échéance pour facture {} à: {}", factureId, clientEmail);

        FactureResponse facture = factureService.getFactureById(factureId);

        Map<String, Object> variables = new HashMap<>();
        variables.put("facture", facture);
        variables.put("numeroFacture", facture.getNumeroFacture());
        variables.put("montantRestant", facture.getMontantRestant());
        variables.put("dateEcheance", facture.getDateEcheance());
        variables.put("joursAvantEcheance", joursAvantEcheance);
        variables.put("client", facture.getNomClient());
        variables.put("baseUrl", baseUrl);

        String subject = String.format("Échéance dans %d jour(s) - Facture %s",
                joursAvantEcheance, facture.getNumeroFacture());

        sendHtmlEmail(clientEmail, subject, "rappel-paiement", variables);
    }

    @Override
    public void sendFactureEnRetardEmail(UUID factureId, String clientEmail, int joursDeRetard) {
        log.info("Envoi d'email de facture en retard {} à: {}", factureId, clientEmail);

        FactureResponse facture = factureService.getFactureById(factureId);

        Map<String, Object> variables = new HashMap<>();
        variables.put("facture", facture);
        variables.put("numeroFacture", facture.getNumeroFacture());
        variables.put("montantRestant", facture.getMontantRestant());
        variables.put("dateEcheance", facture.getDateEcheance());
        variables.put("joursDeRetard", joursDeRetard);
        variables.put("client", facture.getNomClient());
        variables.put("baseUrl", baseUrl);

        String subject = String.format("URGENT: Facture en retard (%d jour(s)) - %s",
                joursDeRetard, facture.getNumeroFacture());

        sendHtmlEmail(clientEmail, subject, "rappel-paiement", variables);
    }

    @Override
    public void sendFacturePdfEmail(UUID factureId, String clientEmail, String pdfPath) {
        log.info("Envoi d'email avec PDF de facture {} à: {}", factureId, clientEmail);

        FactureResponse facture = factureService.getFactureById(factureId);

        Map<String, Object> variables = new HashMap<>();
        variables.put("facture", facture);
        variables.put("client", facture.getNomClient());
        variables.put("numeroFacture", facture.getNumeroFacture());
        variables.put("baseUrl", baseUrl);

        String subject = String.format("Facture %s", facture.getNumeroFacture());
        String attachmentName = String.format("Facture_%s.pdf", facture.getNumeroFacture());

        sendHtmlEmailWithAttachment(clientEmail, subject, "facture-creation",
                variables, pdfPath, attachmentName);

        // Marquer la facture comme envoyée
        factureService.marquerEnvoyeeParEmail(factureId);
    }

    @Override
    public void sendWelcomeClientEmail(UUID clientId, String clientEmail) {
        log.info("Envoi d'email de bienvenue au client {} à: {}", clientId, clientEmail);

        ClientResponse client = clientService.getClientById(clientId);

        Map<String, Object> variables = new HashMap<>();
        variables.put("client", client);
        variables.put("nomClient", client.getUsername());
        variables.put("baseUrl", baseUrl);

        String subject = String.format("Bienvenue %s !", client.getUsername());

        sendHtmlEmail(clientEmail, subject, "welcome-client", variables);
    }

    @Override
    public void sendInternalNotificationEmail(String subject, String message) {
        log.info("Envoi de notification interne: {}", subject);

        // Envoyer à l'administrateur (à configurer)
        String adminEmail = fromEmail; // Ou une configuration spécifique

        sendSimpleEmail(adminEmail, "[NOTIFICATION INTERNE] " + subject, message);
    }

    @Override
    public void sendMonthlyReportEmail(String to, int month, int year) {
        log.info("Envoi du rapport mensuel {}/{} à: {}", month, year, to);

        Map<String, Object> variables = new HashMap<>();
        variables.put("month", month);
        variables.put("year", year);
        variables.put("reportDate", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        variables.put("baseUrl", baseUrl);

        String subject = String.format("Rapport mensuel %02d/%d", month, year);

        sendHtmlEmail(to, subject, "monthly-report", variables);
    }

    @Override
    public void testEmailConfiguration(String testRecipient) {
        log.info("Test de configuration email vers: {}", testRecipient);

        String subject = "Test de configuration email - " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        String message = "Ceci est un email de test pour vérifier la configuration de l'envoi d'emails.\n\n" +
                "Si vous recevez ce message, la configuration fonctionne correctement.\n\n" +
                "Timestamp: " + LocalDateTime.now();

        sendSimpleEmail(testRecipient, subject, message);
    }

    @Override
    public boolean isValidEmail(String email) {
        return ValidationUtil.isValidEmail(email);
    }

    // Méthodes privées utilitaires

    private String processTemplate(String templateName, Map<String, Object> variables) {
        try {
            Context context = new Context(Locale.getDefault());
            if (variables != null) {
                context.setVariables(variables);
            }

            return templateEngine.process(templateName, context);
        } catch (Exception e) {
            log.error("Erreur lors du traitement du template {}: {}", templateName, e.getMessage(), e);
            throw new EmailException.EmailTemplateException(templateName, e);
        }
    }

    private void validateEmailParameters(String to, String subject) {
        if (!ValidationUtil.isValidEmail(to)) {
            throw new EmailException("Adresse email destinataire invalide: " + to, "INVALID_EMAIL");
        }

        if (ValidationUtil.isBlank(subject)) {
            throw new EmailException("Le sujet de l'email ne peut pas être vide", "EMPTY_SUBJECT");
        }

        if (ValidationUtil.isBlank(fromEmail)) {
            throw new EmailException.EmailConfigurationException("L'adresse email expéditrice n'est pas configurée");
        }
    }

    private void validateTemplate(String templateName) {
        if (ValidationUtil.isBlank(templateName)) {
            throw new EmailException("Le nom du template ne peut pas être vide", "EMPTY_TEMPLATE_NAME");
        }
    }

    private void validateAttachment(String attachmentPath, String attachmentName) {
        if (ValidationUtil.isBlank(attachmentPath)) {
            throw new EmailException("Le chemin de la pièce jointe ne peut pas être vide", "EMPTY_ATTACHMENT_PATH");
        }

        if (ValidationUtil.isBlank(attachmentName)) {
            throw new EmailException("Le nom de la pièce jointe ne peut pas être vide", "EMPTY_ATTACHMENT_NAME");
        }

        File attachmentFile = new File(attachmentPath);
        if (!attachmentFile.exists()) {
            throw new EmailException("La pièce jointe n'existe pas: " + attachmentPath, "ATTACHMENT_NOT_FOUND");
        }

        if (!attachmentFile.canRead()) {
            throw new EmailException("Impossible de lire la pièce jointe: " + attachmentPath, "ATTACHMENT_NOT_READABLE");
        }
    }
}