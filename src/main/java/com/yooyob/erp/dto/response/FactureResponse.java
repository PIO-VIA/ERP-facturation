package com.yooyob.erp.dto.response;

import com.yooyob.erp.model.enums.StatutFacture;
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
public class FactureResponse {

    private UUID idFacture;
    private String numeroFacture;
    private LocalDate dateFacturation;
    private LocalDate dateEcheance;
    private String type;
    private StatutFacture etat;
    private BigDecimal montantTotal;
    private BigDecimal montantRestant;
    private UUID idClient;
    private String nomClient;
    private String adresseClient;
    private String emailClient;
    private String telephoneClient;
    private List<LigneFactureResponse> lignesFacture;
    private BigDecimal montantHT;
    private BigDecimal montantTVA;
    private BigDecimal montantTTC;
    private String devise;
    private BigDecimal tauxChange;
    private String conditionsPaiement;
    private String notes;
    private String referenceCommande;
    private String pdfPath;
    private Boolean envoyeParEmail;
    private LocalDateTime dateEnvoiEmail;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}