package com.yooyob.erp.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeviseUpdateRequest {

    private String nomDevise;
    private String symbole;
    private Boolean actif;
    private String uniteMonetaire;
    private String sousUniteMonetaire;

    @Positive(message = "Le facteur de conversion doit être positif")
    private BigDecimal facteurConversion;

    private String nomMesure;
}