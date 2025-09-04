package com.yooyob.erp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeviseResponse {

    private UUID idDevise;
    private String nomDevise;
    private String symbole;
    private Boolean actif;
    private String uniteMonetaire;
    private String sousUniteMonetaire;
    private BigDecimal facteurConversion;
    private String nomMesure;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}