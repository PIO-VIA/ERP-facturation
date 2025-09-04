package com.yooyob.erp.dto.request;

import com.yooyob.erp.model.enums.StatutFacture;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FactureCreateRequest {

    @NotNull(message = "La date de facturation est obligatoire")
    private LocalDate dateFacturation;

    @NotNull(message = "La date d'échéance est obligatoire")
    private LocalDate dateEcheance;

    private String type;

    @Builder.Default
    private StatutFacture etat = StatutFacture.BROUILLON;

    @NotNull(message = "L'ID client est obligatoire")
    private UUID idClient;

    @Valid
    private List<LigneFactureCreateRequest> lignesFacture;

    private String devise;

    @Builder.Default
    private BigDecimal tauxChange = BigDecimal.ONE;

    private String conditionsPaiement;

    private String notes;

    private String referenceCommande;
}