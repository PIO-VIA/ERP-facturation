package com.yooyob.erp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RemboursementResponse {

    private UUID idRemboursement;
    private LocalDate dateFacturation;
    private LocalDate dateComptable;
    private String referencePaiement;
    private String banqueDestination;
    private LocalDate dateEcheance;
    private BigDecimal montant;
    private String devise;
    private BigDecimal tauxChange;
    private String motif;
    private String numeroPiece;
    private String statut;
    private UUID idFacture;
    private UUID idClient;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}