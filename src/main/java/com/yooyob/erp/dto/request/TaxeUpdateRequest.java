package com.yooyob.erp.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaxeUpdateRequest {

    private String nomTaxe;

    @PositiveOrZero(message = "Le calcul de la taxe doit être positif ou nul")
    private BigDecimal calculTaxe;

    private Boolean actif;
    private String typeTaxe;
    private String porteTaxe;
    private BigDecimal montant;
    private String positionFiscale;
}