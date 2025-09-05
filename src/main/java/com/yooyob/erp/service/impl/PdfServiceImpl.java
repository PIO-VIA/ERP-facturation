package com.yooyob.erp.service.impl;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.yooyob.erp.dto.response.FactureDetailsResponse;
import com.yooyob.erp.dto.response.PaiementResponse;
import com.yooyob.erp.dto.response.ClientResponse;
import com.yooyob.erp.exception.PdfException;
import com.yooyob.erp.service.PdfService;
import com.yooyob.erp.service.FactureService;
import com.yooyob.erp.service.PaiementService;
import com.yooyob.erp.service.ClientService;
import com.yooyob.erp.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PdfServiceImpl implements PdfService {

    private final SpringTemplateEngine templateEngine;
    private final FactureService factureService;
    private final PaiementService paiementService;
    private final ClientService clientService;

    @Value("${app.pdf.output-directory}")
    private String outputDirectory;

    @Value("${app.pdf.logo-path}")
    private String logoPath;

    private static final String PDF_EXTENSION = ".pdf";

    @Override
    public byte[] generatePdfFromTemplate(String templateName, Map<String, Object> variables) {
        log.info("Génération PDF à partir du template: {}", templateName);

        validateTemplate(templateName);

        try {
            String htmlContent = processTemplate(templateName, variables);
            return generatePdfFromHtml(htmlContent);

        } catch (Exception e) {
            log.error("Erreur lors de la génération PDF avec template {}: {}", templateName, e.getMessage(), e);
            throw new PdfException.PdfGenerationException(templateName, e);
        }
    }

    @Override
    public String generateAndSavePdf(String templateName, Map<String, Object> variables, String fileName) {
        log.info("Génération et sauvegarde PDF: {}", fileName);

        byte[] pdfContent = generatePdfFromTemplate(templateName, variables);
        return savePdfToFile(pdfContent, fileName);
    }

    @Override
    public byte[] generateFacturePdf(UUID factureId) {
        log.info("Génération PDF pour la facture: {}", factureId);

        if (!ValidationUtil.isValidUuid(factureId)) {
            throw new PdfException("ID de facture invalide", "INVALID_FACTURE_ID");
        }

        try {
            FactureDetailsResponse facture = factureService.getFactureDetails(factureId);
            Map<String, Object> variables = prepareFactureVariables(facture);
            return generatePdfFromTemplate("pdf/facture-template", variables);

        } catch (Exception e) {
            log.error("Erreur lors de la génération PDF pour la facture {}: {}", factureId, e.getMessage(), e);
            throw new PdfException.PdfGenerationException("facture " + factureId, e);
        }
    }

    @Override
    public String generateAndSaveFacturePdf(UUID factureId) {
        log.info("Génération et sauvegarde PDF pour la facture: {}", factureId);

        try {
            FactureDetailsResponse facture = factureService.getFactureDetails(factureId);
            String fileName = generateFactureFileName(facture.getNumeroFacture());

            byte[] pdfContent = generateFacturePdf(factureId);
            return savePdfToFile(pdfContent, fileName);

        } catch (Exception e) {
            log.error("Erreur lors de la génération et sauvegarde PDF pour la facture {}: {}", factureId, e.getMessage(), e);
            throw new PdfException.PdfStorageException("facture " + factureId, e);
        }
    }

    @Override
    public byte[] generateRecuPaiementPdf(UUID paiementId) {
        log.info("Génération PDF du reçu pour le paiement: {}", paiementId);

        if (!ValidationUtil.isValidUuid(paiementId)) {
            throw new PdfException("ID de paiement invalide", "INVALID_PAIEMENT_ID");
        }

        try {
            PaiementResponse paiement = paiementService.getPaiementById(paiementId);
            Map<String, Object> variables = prepareRecuVariables(paiement);
            return generatePdfFromTemplate("pdf/recu-paiement", variables);

        } catch (Exception e) {
            log.error("Erreur lors de la génération PDF du reçu pour le paiement {}: {}", paiementId, e.getMessage(), e);
            throw new PdfException.PdfGenerationException("reçu paiement " + paiementId, e);
        }
    }

    @Override
    public String generateAndSaveRecuPaiementPdf(UUID paiementId) {
        log.info("Génération et sauvegarde PDF du reçu pour le paiement: {}", paiementId);

        try {
            PaiementResponse paiement = paiementService.getPaiementById(paiementId);
            String fileName = generateRecuFileName(paiementId);

            byte[] pdfContent = generateRecuPaiementPdf(paiementId);
            return savePdfToFile(pdfContent, fileName);

        } catch (Exception e) {
            log.error("Erreur lors de la génération et sauvegarde PDF du reçu pour le paiement {}: {}", paiementId, e.getMessage(), e);
            throw new PdfException.PdfStorageException("reçu paiement " + paiementId, e);
        }
    }

    @Override
    public byte[] generateRapportFacturesPdf(LocalDate startDate, LocalDate endDate) {
        log.info("Génération PDF du rapport de factures pour la période {} - {}", startDate, endDate);

        try {
            Map<String, Object> variables = prepareRapportFacturesVariables(startDate, endDate);
            return generatePdfFromTemplate("pdf/rapport-factures", variables);

        } catch (Exception e) {
            log.error("Erreur lors de la génération du rapport factures: {}", e.getMessage(), e);
            throw new PdfException.PdfGenerationException("rapport factures", e);
        }
    }

    @Override
    public byte[] generateRapportPaiementsPdf(LocalDate startDate, LocalDate endDate) {
        log.info("Génération PDF du rapport de paiements pour la période {} - {}", startDate, endDate);

        try {
            Map<String, Object> variables = prepareRapportPaiementsVariables(startDate, endDate);
            return generatePdfFromTemplate("pdf/rapport-paiements", variables);

        } catch (Exception e) {
            log.error("Erreur lors de la génération du rapport paiements: {}", e.getMessage(), e);
            throw new PdfException.PdfGenerationException("rapport paiements", e);
        }
    }

    @Override
    public byte[] generateRapportClientPdf(UUID clientId) {
        log.info("Génération PDF du rapport client: {}", clientId);

        try {
            ClientResponse client = clientService.getClientById(clientId);
            Map<String, Object> variables = prepareRapportClientVariables(client);
            return generatePdfFromTemplate("pdf/rapport-client", variables);

        } catch (Exception e) {
            log.error("Erreur lors de la génération du rapport client {}: {}", clientId, e.getMessage(), e);
            throw new PdfException.PdfGenerationException("rapport client " + clientId, e);
        }
    }

    @Override
    public byte[] generateRapportMensuelPdf(int month, int year) {
        log.info("Génération PDF du rapport mensuel {}/{}", month, year);

        try {
            Map<String, Object> variables = prepareRapportMensuelVariables(month, year);
            return generatePdfFromTemplate("pdf/rapport-mensuel", variables);

        } catch (Exception e) {
            log.error("Erreur lors de la génération du rapport mensuel {}/{}: {}", month, year, e.getMessage(), e);
            throw new PdfException.PdfGenerationException("rapport mensuel", e);
        }
    }

    @Override
    public byte[] generateEtatCompteClientPdf(UUID clientId, LocalDate startDate, LocalDate endDate) {
        log.info("Génération PDF de l'état de compte client {} pour la période {} - {}", clientId, startDate, endDate);

        try {
            ClientResponse client = clientService.getClientById(clientId);
            Map<String, Object> variables = prepareEtatCompteVariables(client, startDate, endDate);
            return generatePdfFromTemplate("pdf/etat-compte", variables);

        } catch (Exception e) {
            log.error("Erreur lors de la génération de l'état de compte client {}: {}", clientId, e.getMessage(), e);
            throw new PdfException.PdfGenerationException("état de compte client " + clientId, e);
        }
    }

    @Override
    public byte[] readPdfFile(String filePath) {
        log.debug("Lecture du fichier PDF: {}", filePath);

        try {
            Path path = Paths.get(filePath);
            if (!Files.exists(path)) {
                throw new PdfException("Le fichier PDF n'existe pas: " + filePath, "FILE_NOT_FOUND");
            }

            return Files.readAllBytes(path);

        } catch (IOException e) {
            log.error("Erreur lors de la lecture du fichier PDF {}: {}", filePath, e.getMessage(), e);
            throw new PdfException("Impossible de lire le fichier PDF", "FILE_READ_ERROR", e);
        }
    }

    @Override
    public void deletePdfFile(String filePath) {
        log.info("Suppression du fichier PDF: {}", filePath);

        try {
            Path path = Paths.get(filePath);
            if (Files.exists(path)) {
                Files.delete(path);
                log.info("Fichier PDF supprimé: {}", filePath);
            } else {
                log.warn("Tentative de suppression d'un fichier inexistant: {}", filePath);
            }

        } catch (IOException e) {
            log.error("Erreur lors de la suppression du fichier PDF {}: {}", filePath, e.getMessage(), e);
            throw new PdfException("Impossible de supprimer le fichier PDF", "FILE_DELETE_ERROR", e);
        }
    }

    @Override
    public boolean pdfFileExists(String filePath) {
        if (ValidationUtil.isBlank(filePath)) {
            return false;
        }

        try {
            return Files.exists(Paths.get(filePath));
        } catch (Exception e) {
            log.warn("Erreur lors de la vérification d'existence du fichier {}: {}", filePath, e.getMessage());
            return false;
        }
    }

    @Override
    public String getDefaultSavePath() {
        return outputDirectory;
    }

    @Override
    public String generateUniqueFileName(String prefix, String extension) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String uniqueId = UUID.randomUUID().toString().substring(0, 8);
        return String.format("%s_%s_%s%s", prefix, timestamp, uniqueId, extension);
    }

    // Méthodes privées utilitaires

    private String processTemplate(String templateName, Map<String, Object> variables) {
        try {
            Context context = new Context(Locale.getDefault());
            if (variables != null) {
                context.setVariables(variables);
            }

            // Ajouter des variables globales
            context.setVariable("logoPath", logoPath);
            context.setVariable("generatedDate", LocalDateTime.now());
            context.setVariable("dateGeneration", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));

            return templateEngine.process(templateName, context);

        } catch (Exception e) {
            log.error("Erreur lors du traitement du template {}: {}", templateName, e.getMessage(), e);
            throw new PdfException.PdfTemplateException(templateName, e);
        }
    }

    private byte[] generatePdfFromHtml(String htmlContent) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.withHtmlContent(htmlContent, null);
            builder.toStream(outputStream);
            builder.useFastMode();

            // Configuration additionnelle
            builder.useDefaultPageSize(210, 297, PdfRendererBuilder.PageSizeUnits.MM);

            builder.run();

            byte[] pdfBytes = outputStream.toByteArray();
            log.debug("PDF généré avec succès, taille: {} bytes", pdfBytes.length);

            return pdfBytes;

        } catch (Exception e) {
            log.error("Erreur lors de la conversion HTML vers PDF: {}", e.getMessage(), e);
            throw new PdfException("Erreur lors de la génération du PDF", "PDF_CONVERSION_ERROR", e);
        }
    }

    private String savePdfToFile(byte[] pdfContent, String fileName) {
        try {
            // Créer le répertoire de sortie s'il n'existe pas
            Path outputPath = Paths.get(outputDirectory);
            if (!Files.exists(outputPath)) {
                Files.createDirectories(outputPath);
            }

            // Nettoyer le nom de fichier et ajouter l'extension si nécessaire
            String cleanFileName = cleanFileName(fileName);
            if (!cleanFileName.toLowerCase().endsWith(PDF_EXTENSION)) {
                cleanFileName += PDF_EXTENSION;
            }

            Path filePath = outputPath.resolve(cleanFileName);
            Files.write(filePath, pdfContent);

            String fullPath = filePath.toAbsolutePath().toString();
            log.info("PDF sauvegardé: {}", fullPath);

            return fullPath;

        } catch (IOException e) {
            log.error("Erreur lors de la sauvegarde du PDF {}: {}", fileName, e.getMessage(), e);
            throw new PdfException.PdfStorageException(fileName, e);
        }
    }

    private Map<String, Object> prepareFactureVariables(FactureDetailsResponse facture) {
        Map<String, Object> variables = new HashMap<>();

        // Informations principales
        variables.put("facture", facture);
        variables.put("client", facture.getClient());
        variables.put("lignesFacture", facture.getLignesFacture());

        // Formatage des dates
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        variables.put("dateFacturationFormatted", facture.getDateFacturation().format(dateFormatter));
        variables.put("dateEcheanceFormatted", facture.getDateEcheance().format(dateFormatter));

        // Informations supplémentaires
        variables.put("entreprise", getEntrepriseInfo());

        return variables;
    }

    private Map<String, Object> prepareRecuVariables(PaiementResponse paiement) {
        Map<String, Object> variables = new HashMap<>();

        // Informations du paiement
        variables.put("paiement", paiement);

        // Client et facture associée si disponible
        if (paiement.getIdClient() != null) {
            try {
                ClientResponse client = clientService.getClientById(paiement.getIdClient());
                variables.put("client", client);
            } catch (Exception e) {
                log.warn("Impossible de récupérer le client {}: {}", paiement.getIdClient(), e.getMessage());
            }
        }

        if (paiement.getIdFacture() != null) {
            try {
                FactureDetailsResponse facture = factureService.getFactureDetails(paiement.getIdFacture());
                variables.put("facture", facture);
            } catch (Exception e) {
                log.warn("Impossible de récupérer la facture {}: {}", paiement.getIdFacture(), e.getMessage());
            }
        }

        // Formatage des dates
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        variables.put("datePaiementFormatted", paiement.getDate().format(dateFormatter));

        variables.put("entreprise", getEntrepriseInfo());

        return variables;
    }

    private Map<String, Object> prepareRapportFacturesVariables(LocalDate startDate, LocalDate endDate) {
        Map<String, Object> variables = new HashMap<>();

        variables.put("startDate", startDate);
        variables.put("endDate", endDate);
        variables.put("factures", factureService.getFacturesByPeriode(startDate, endDate));
        variables.put("stats", factureService.getFactureStatistics(startDate, endDate));
        variables.put("entreprise", getEntrepriseInfo());

        return variables;
    }

    private Map<String, Object> prepareRapportPaiementsVariables(LocalDate startDate, LocalDate endDate) {
        Map<String, Object> variables = new HashMap<>();

        variables.put("startDate", startDate);
        variables.put("endDate", endDate);
        variables.put("paiements", paiementService.getPaiementsByPeriode(startDate, endDate));
        variables.put("totalPaiements", paiementService.getTotalPaiementsByPeriode(startDate, endDate));
        variables.put("entreprise", getEntrepriseInfo());

        return variables;
    }

    private Map<String, Object> prepareRapportClientVariables(ClientResponse client) {
        Map<String, Object> variables = new HashMap<>();

        variables.put("client", client);
        variables.put("factures", factureService.getFacturesByClient(client.getIdClient()));
        variables.put("paiements", paiementService.getPaiementsByClient(client.getIdClient()));
        variables.put("totalFactures", factureService.countFacturesByClient(client.getIdClient()));
        variables.put("totalPaiements", paiementService.getTotalPaiementsByClient(client.getIdClient()));
        variables.put("entreprise", getEntrepriseInfo());

        return variables;
    }

    private Map<String, Object> prepareRapportMensuelVariables(int month, int year) {
        Map<String, Object> variables = new HashMap<>();

        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        variables.put("month", month);
        variables.put("year", year);
        variables.put("startDate", startDate);
        variables.put("endDate", endDate);
        variables.put("chiffreAffaires", factureService.getChiffreAffairesByMonth(year));
        variables.put("stats", factureService.getFactureStatistics(startDate, endDate));
        variables.put("entreprise", getEntrepriseInfo());

        return variables;
    }

    private Map<String, Object> prepareEtatCompteVariables(ClientResponse client, LocalDate startDate, LocalDate endDate) {
        Map<String, Object> variables = new HashMap<>();

        variables.put("client", client);
        variables.put("startDate", startDate);
        variables.put("endDate", endDate);
        variables.put("factures", factureService.getFacturesByClient(client.getIdClient()));
        variables.put("paiements", paiementService.getPaiementsByClientAndPeriode(client.getIdClient(), startDate, endDate));
        variables.put("entreprise", getEntrepriseInfo());

        return variables;
    }

    private Map<String, Object> getEntrepriseInfo() {
        Map<String, Object> entreprise = new HashMap<>();

        entreprise.put("nom", "YooYob ERP");
        entreprise.put("adresse", "123 Rue de l'Exemple, 75001 Paris");
        entreprise.put("telephone", "+33 1 23 45 67 89");
        entreprise.put("email", "contact@yooyob.com");
        entreprise.put("siret", "123 456 789 00012");
        entreprise.put("numeroTVA", "FR12345678901");
        entreprise.put("siteWeb", "www.yooyob.com");

        return entreprise;
    }

    private String generateFactureFileName(String numeroFacture) {
        String cleanNumero = numeroFacture.replaceAll("[^a-zA-Z0-9]", "_");
        return String.format("Facture_%s_%s", cleanNumero,
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")));
    }

    private String generateRecuFileName(UUID paiementId) {
        return String.format("Recu_Paiement_%s_%s",
                paiementId.toString().substring(0, 8),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")));
    }

    private String cleanFileName(String fileName) {
        if (ValidationUtil.isBlank(fileName)) {
            return "document_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        }

        return fileName.replaceAll("[^a-zA-Z0-9._-]", "_");
    }

    private void validateTemplate(String templateName) {
        if (ValidationUtil.isBlank(templateName)) {
            throw new PdfException("Le nom du template ne peut pas être vide", "EMPTY_TEMPLATE_NAME");
        }
    }
}