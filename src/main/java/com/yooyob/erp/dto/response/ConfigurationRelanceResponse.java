package com.yooyob.erp.dto.response;

import com.yooyob.erp.model.enums.TypeRelance;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConfigurationRelanceResponse {

    private UUID idConfiguration;
    private String nomConfiguration;
    private String description;
    private TypeRelance typeRelance;
    private Integer joursAvantEcheance;
    private Integer joursApresEcheance;
    private BigDecimal montantMinimum;
    private BigDecimal montantMaximum;
    private String templateEmail;
    private String objetEmail;
    private String contenuPersonnalise;
    private Boolean envoyerParEmail;
    private Boolean envoyerParSms;
    private Boolean genererPdf;
    private String templatePdf;
    private Boolean inclureFacturePdf;
    private List<String> copieInterne;
    private Integer heureEnvoi;
    private List<Integer> joursSemaineEnvoi;
    private Boolean exclureWeekends;
    private Boolean exclureJoursFeries;
    private Integer delaiMinEntreRelances;
    private Integer nombreMaxRelances;
    private Boolean actif;
    private Integer ordrePriorite;
    private List<String> conditionsArret;
    private Boolean escaladeAutomatique;
    private List<String> escaladeVers;
    private Integer delaiEscaladeJours;
    private BigDecimal fraisRelance;
    private Boolean appliquerFraisAutomatiquement;
    private UUID createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}