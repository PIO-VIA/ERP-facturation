package com.yooyob.erp.service;

import java.util.Map;
import java.util.UUID;

public interface PdfService {

    /**
     * Génère un PDF à partir d'un template HTML
     */
    byte[] generatePdfFromTemplate(String templateName, Map<String, Object> variables);

    /**
     * Génère et sauvegarde un PDF
     */
    String generateAndSavePdf(String templateName, Map<String, Object> variables, String fileName);

    /**
     * Génère le PDF d'une facture
     */
    byte[] generateFacturePdf(UUID factureId);

    /**
     * Génère et sauvegarde le PDF d'une facture
     */
    String generateAndSaveFacturePdf(UUID factureId);

    /**
     * Génère le PDF d'un reçu de paiement
     */
    byte[] generateRecuPaiementPdf(UUID paiementId);

    /**
     * Génère et sauvegarde le PDF d'un reçu de paiement
     */
    String generateAndSaveRecuPaiementPdf(UUID paiementId);

    /**
     * Génère un rapport de factures par période
     */
    byte[] generateRapportFacturesPdf(java.time.LocalDate startDate, java.time.LocalDate endDate);

    /**
     * Génère un rapport de paiements par période
     */
    byte[] generateRapportPaiementsPdf(java.time.LocalDate startDate, java.time.LocalDate endDate);

    /**
     * Génère un rapport client
     */
    byte[] generateRapportClientPdf(UUID clientId);

    /**
     * Génère un rapport mensuel
     */
    byte[] generateRapportMensuelPdf(int month, int year);

    /**
     * Génère un état de compte client
     */
    byte[] generateEtatCompteClientPdf(UUID clientId, java.time.LocalDate startDate, java.time.LocalDate endDate);

    /**
     * Lit un fichier PDF existant
     */
    byte[] readPdfFile(String filePath);

    /**
     * Supprime un fichier PDF
     */
    void deletePdfFile(String filePath);

    /**
     * Vérifie si un fichier PDF existe
     */
    boolean pdfFileExists(String filePath);

    /**
     * Obtient le chemin de sauvegarde par défaut
     */
    String getDefaultSavePath();

    /**
     * Génère un nom de fichier unique
     */
    String generateUniqueFileName(String prefix, String extension);
}