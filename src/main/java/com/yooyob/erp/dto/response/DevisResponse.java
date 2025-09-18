package com.yooyob.erp.dto.response;

import com.yooyob.erp.model.enums.StatutDevis;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DevisResponse {

    private UUID idDevis;
    private String numeroDevis;
    private LocalDate dateCreation;
    private LocalDate dateValidite;
    private String type;
    private StatutDevis statut;
    private BigDecimal montantTotal;
    private UUID idClient;
    private String nomClient;
    private String adresseClient;
    private String emailClient;
    private String telephoneClient;
    private List<LigneDevisResponse> lignesDevis;
    private BigDecimal montantHT;
    private BigDecimal montantTVA;
    private BigDecimal montantTTC;
    private String devise;
    private BigDecimal tauxChange;
    private String conditionsPaiement;
    private String notes;
    private String referenceExterne;
    private String pdfPath;
    private Boolean envoyeParEmail;
    private LocalDateTime dateEnvoiEmail;
    private LocalDateTime dateAcceptation;
    private LocalDateTime dateRefus;
    private String motifRefus;
    private UUID idFactureConvertie;
    private BigDecimal remiseGlobalePourcentage;
    private BigDecimal remiseGlobaleMontant;
    private Integer validiteOffreJours;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}