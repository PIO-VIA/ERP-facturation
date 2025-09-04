package com.yooyob.erp.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RemboursementCreateRequest {

    @NotNull(message = "La date de facturation est obligatoire")
    private LocalDate dateFacturation;

    @NotNull(message = "La date comptable est obligatoire")
    private LocalDate dateComptable;

    private String referencePaiement;

    private String banqueDestination;

    @NotNull(message = "La date d'échéance est obligatoire")
    private LocalDate dateEcheance;

    @NotNull(message = "Le montant est obligatoire")
    @Positive(message = "Le montant doit être positif")
    private BigDecimal montant;

    private String devise;

    @Builder.Default
    private BigDecimal tauxChange = BigDecimal.ONE;

    private String motif;

    private String numeroPiece;

    private UUID idFacture;

    private UUID idClient;
}