package com.yooyob.erp.dto.response;

import com.yooyob.erp.model.enums.StatutAvoir;
import com.yooyob.erp.model.enums.TypeAvoir;
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
public class FactureAvoirResponse {

    private UUID idAvoir;
    private String numeroAvoir;
    private LocalDate dateCreation;
    private LocalDate dateValidation;
    private TypeAvoir typeAvoir;
    private StatutAvoir statut;
    private BigDecimal montantTotal;
    private UUID idFactureOrigine;
    private String numeroFactureOrigine;
    private UUID idClient;
    private String nomClient;
    private String adresseClient;
    private String emailClient;
    private String telephoneClient;
    private List<LigneAvoirResponse> lignesAvoir;
    private BigDecimal montantHT;
    private BigDecimal montantTVA;
    private BigDecimal montantTTC;
    private String devise;
    private BigDecimal tauxChange;
    private String motifAvoir;
    private String notes;
    private String pdfPath;
    private Boolean envoyeParEmail;
    private LocalDateTime dateEnvoiEmail;
    private LocalDateTime dateApplication;
    private BigDecimal montantApplique;
    private BigDecimal montantRembourse;
    private String modeRemboursement;
    private String referenceRemboursement;
    private LocalDateTime dateRemboursement;
    private UUID approuvePar;
    private LocalDateTime dateApprobation;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}