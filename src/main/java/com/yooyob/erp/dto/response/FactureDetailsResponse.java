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
public class FactureDetailsResponse {

    private UUID idFacture;
    private String numeroFacture;
    private LocalDate dateFacturation;
    private LocalDate dateEcheance;
    private String type;
    private StatutFacture etat;
    private BigDecimal montantTotal;
    private BigDecimal montantRestant;

    // Informations client détaillées
    private ClientResponse client;

    // Lignes de facture détaillées
    private List<LigneFactureResponse> lignesFacture;

    // Détails financiers
    private BigDecimal montantHT;
    private BigDecimal montantTVA;
    private BigDecimal montantTTC;
    private String devise;
    private BigDecimal tauxChange;

    // Informations complémentaires
    private String conditionsPaiement;
    private String notes;
    private String referenceCommande;
    private String pdfPath;
    private Boolean envoyeParEmail;
    private LocalDateTime dateEnvoiEmail;

    // Paiements associés
    private List<PaiementResponse> paiements;
    private BigDecimal totalPaiements;

    // Audit
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}