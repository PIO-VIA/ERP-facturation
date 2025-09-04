package com.yooyob.erp.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaxeCreateRequest {

    @NotBlank(message = "Le nom de la taxe est obligatoire")
    private String nomTaxe;

    @NotNull(message = "Le calcul de la taxe est obligatoire")
    @PositiveOrZero(message = "Le calcul de la taxe doit être positif ou nul")
    private BigDecimal calculTaxe;

    @Builder.Default
    private Boolean actif = true;

    @NotBlank(message = "Le type de taxe est obligatoire")
    private String typeTaxe;

    private String porteTaxe;

    @NotNull(message = "Le montant est obligatoire")
    private BigDecimal montant;

    private String positionFiscale;
}