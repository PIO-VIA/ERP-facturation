package com.yooyob.erp.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LigneDevisCreateRequest {

    @NotNull(message = "La quantité est obligatoire")
    @Positive(message = "La quantité doit être positive")
    private Integer quantite;

    private String description;

    @NotNull(message = "Le débit est obligatoire")
    @PositiveOrZero(message = "Le débit doit être positif ou nul")
    private BigDecimal debit;

    @NotNull(message = "Le crédit est obligatoire")
    @PositiveOrZero(message = "Le crédit doit être positif ou nul")
    private BigDecimal credit;

    @Builder.Default
    private Boolean isTaxLine = false;

    private UUID idProduit;

    private String nomProduit;

    @PositiveOrZero(message = "Le prix unitaire doit être positif ou nul")
    private BigDecimal prixUnitaire;

    @PositiveOrZero(message = "Le montant total doit être positif ou nul")
    private BigDecimal montantTotal;

    @PositiveOrZero(message = "La remise en pourcentage doit être positive ou nulle")
    private BigDecimal remisePourcentage;

    @PositiveOrZero(message = "La remise en montant doit être positive ou nulle")
    private BigDecimal remiseMontant;
}