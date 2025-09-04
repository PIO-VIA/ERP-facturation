package com.yooyob.erp.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RemboursementUpdateRequest {

    private LocalDate dateFacturation;
    private LocalDate dateComptable;
    private String referencePaiement;
    private String banqueDestination;
    private LocalDate dateEcheance;

    @Positive(message = "Le montant doit être positif")
    private BigDecimal montant;

    private String devise;
    private BigDecimal tauxChange;
    private String motif;
    private String numeroPiece;
    private String statut;
    private UUID idFacture;
    private UUID idClient;
}