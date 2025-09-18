package com.yooyob.erp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatistiqueRelanceResponse {
    private Long totalRelancesEnvoyees;
    private Long totalRelancesReussies;
    private Long totalRelancesEchouees;
    private Double tauxReponse;
    private Double tauxRecouvrementApresRelance;
    private Integer delaiMoyenReponse;
}