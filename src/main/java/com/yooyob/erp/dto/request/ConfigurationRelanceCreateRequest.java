package com.yooyob.erp.dto.request;

import com.yooyob.erp.model.enums.TypeRelance;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConfigurationRelanceCreateRequest {

    @NotBlank(message = "Le nom de la configuration est obligatoire")
    private String nomConfiguration;

    private String description;

    @NotNull(message = "Le type de relance est obligatoire")
    private TypeRelance typeRelance;

    private Integer joursAvantEcheance;

    private Integer joursApresEcheance;

    private BigDecimal montantMinimum;

    private BigDecimal montantMaximum;

    private String templateEmail;

    private String objetEmail;

    private String contenuPersonnalise;

    @Builder.Default
    private Boolean envoyerParEmail = true;

    @Builder.Default
    private Boolean envoyerParSms = false;

    @Builder.Default
    private Boolean genererPdf = false;

    private String templatePdf;

    @Builder.Default
    private Boolean inclureFacturePdf = true;

    private List<String> copieInterne;

    @Builder.Default
    private Integer heureEnvoi = 9;

    private List<Integer> joursSemaineEnvoi;

    @Builder.Default
    private Boolean exclureWeekends = true;

    @Builder.Default
    private Boolean exclureJoursFeries = true;

    @Builder.Default
    private Integer delaiMinEntreRelances = 7;

    @Builder.Default
    private Integer nombreMaxRelances = 3;

    @Builder.Default
    private Boolean actif = true;

    @Builder.Default
    private Integer ordrePriorite = 1;

    private List<String> conditionsArret;

    @Builder.Default
    private Boolean escaladeAutomatique = false;

    private List<String> escaladeVers;

    private Integer delaiEscaladeJours;

    private BigDecimal fraisRelance;

    @Builder.Default
    private Boolean appliquerFraisAutomatiquement = false;
}