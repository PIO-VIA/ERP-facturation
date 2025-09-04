package com.yooyob.erp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatistiqueResponse {

    private BigDecimal chiffreAffairesTotal;
    private BigDecimal chiffreAffairesMois;
    private BigDecimal chiffreAffairesAnnee;
    private Integer nombreFactures;
    private Integer nombreClients;
    private BigDecimal montantImpaye;
    private BigDecimal montantEnRetard;
    private List<ChiffreAffairesMensuel> evolutionMensuelle;
    private List<TopClient> topClients;
    private Map<String, BigDecimal> repartitionParDevise;
    private LocalDate derniereMiseAJour;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ChiffreAffairesMensuel {
        private String mois;
        private BigDecimal montant;
        private Integer nombreFactures;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TopClient {
        private String nomClient;
        private BigDecimal montantTotal;
        private Integer nombreFactures;
    }
}