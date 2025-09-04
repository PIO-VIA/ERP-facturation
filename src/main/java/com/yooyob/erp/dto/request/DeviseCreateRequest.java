package com.yooyob.erp.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeviseCreateRequest {

    @NotBlank(message = "Le nom de la devise est obligatoire")
    private String nomDevise;

    @NotBlank(message = "Le symbole est obligatoire")
    private String symbole;

    @Builder.Default
    private Boolean actif = true;

    private String uniteMonetaire;

    private String sousUniteMonetaire;

    @NotNull(message = "Le facteur de conversion est obligatoire")
    @Positive(message = "Le facteur de conversion doit être positif")
    private BigDecimal facteurConversion;

    private String nomMesure;
}