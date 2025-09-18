package com.yooyob.erp.dto.request;

import com.yooyob.erp.model.enums.StatutAvoir;
import com.yooyob.erp.model.enums.TypeAvoir;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FactureAvoirCreateRequest {

    @NotNull(message = "La date de création est obligatoire")
    private LocalDate dateCreation;

    @NotNull(message = "Le type d'avoir est obligatoire")
    private TypeAvoir typeAvoir;

    private StatutAvoir statut;

    @NotNull(message = "L'ID de la facture d'origine est obligatoire")
    private UUID idFactureOrigine;

    @Valid
    private List<LigneAvoirCreateRequest> lignesAvoir;

    private String devise;

    private BigDecimal tauxChange;

    @NotNull(message = "Le motif de l'avoir est obligatoire")
    private String motifAvoir;

    private String notes;

    private String modeRemboursement;

    private String referenceRemboursement;
}