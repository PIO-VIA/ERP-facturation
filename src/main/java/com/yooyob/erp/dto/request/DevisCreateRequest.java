package com.yooyob.erp.dto.request;

import com.yooyob.erp.model.enums.StatutDevis;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
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
public class DevisCreateRequest {

    @NotNull(message = "La date de création est obligatoire")
    private LocalDate dateCreation;

    @NotNull(message = "La date de validité est obligatoire")
    private LocalDate dateValidite;

    private String type;

    private StatutDevis statut;

    @NotNull(message = "L'ID client est obligatoire")
    private UUID idClient;

    @Valid
    private List<LigneDevisCreateRequest> lignesDevis;

    private String devise;

    private BigDecimal tauxChange;

    private String conditionsPaiement;

    private String notes;

    private String referenceExterne;

    @PositiveOrZero(message = "La remise globale en pourcentage doit être positive ou nulle")
    private BigDecimal remiseGlobalePourcentage;

    @PositiveOrZero(message = "La remise globale en montant doit être positive ou nulle")
    private BigDecimal remiseGlobaleMontant;

    private Integer validiteOffreJours;
}