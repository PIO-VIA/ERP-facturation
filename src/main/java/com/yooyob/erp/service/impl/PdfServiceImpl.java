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
            // Traitement du template HTML
            String htmlContent = processTemplate(templateName, variables);

            // Génération du PDF
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

            return generatePdfFromTemplate("facture-template", variables);

        } catch (Exception e) {
            log.error("Erreur lors de la génération PDF pour la facture {}: