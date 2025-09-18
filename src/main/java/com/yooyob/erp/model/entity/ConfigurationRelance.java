package com.yooyob.erp.model.entity;

import com.yooyob.erp.model.enums.TypeRelance;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("configurations_relance")
public class ConfigurationRelance {

    @PrimaryKey
    @Column("id_configuration")
    private UUID idConfiguration;

    @NotBlank(message = "Le nom de la configuration est obligatoire")
    @Column("nom_configuration")
    private String nomConfiguration;

    @Column("description")
    private String description;

    @NotNull(message = "Le type de relance est obligatoire")
    @Column("type_relance")
    private TypeRelance typeRelance;

    @Column("jours_avant_echeance")
    private Integer joursAvantEcheance;

    @Column("jours_apres_echeance")
    private Integer joursApresEcheance;

    @Column("montant_minimum")
    private BigDecimal montantMinimum;

    @Column("montant_maximum")
    private BigDecimal montantMaximum;

    @Column("template_email")
    private String templateEmail;

    @Column("objet_email")
    private String objetEmail;

    @Column("contenu_personnalise")
    private String contenuPersonnalise;

    @Column("envoyer_par_email")
    @Builder.Default
    private Boolean envoyerParEmail = true;

    @Column("envoyer_par_sms")
    @Builder.Default
    private Boolean envoyerParSms = false;

    @Column("generer_pdf")
    @Builder.Default
    private Boolean genererPdf = false;

    @Column("template_pdf")
    private String templatePdf;

    @Column("inclure_facture_pdf")
    @Builder.Default
    private Boolean inclureFacturePdf = true;

    @Column("copie_interne")
    private List<String> copieInterne;

    @Column("heure_envoi")
    @Builder.Default
    private Integer heureEnvoi = 9; // 9h du matin

    @Column("jours_semaine_envoi")
    private List<Integer> joursSemaineEnvoi; // 1-7 pour lundi-dimanche

    @Column("exclure_weekends")
    @Builder.Default
    private Boolean exclureWeekends = true;

    @Column("exclure_jours_feries")
    @Builder.Default
    private Boolean exclureJoursFeries = true;

    @Column("delai_min_entre_relances")
    @Builder.Default
    private Integer delaiMinEntreRelances = 7; // jours

    @Column("nombre_max_relances")
    @Builder.Default
    private Integer nombreMaxRelances = 3;

    @Column("actif")
    @Builder.Default
    private Boolean actif = true;

    @Column("ordre_priorite")
    @Builder.Default
    private Integer ordrePriorite = 1;

    @Column("conditions_arret")
    private List<String> conditionsArret; // "PAIEMENT_RECU", "AVOIR_EMIS", etc.

    @Column("escalade_automatique")
    @Builder.Default
    private Boolean escaladeAutomatique = false;

    @Column("escalade_vers")
    private List<String> escaladeVers; // emails ou rôles

    @Column("delai_escalade_jours")
    private Integer delaiEscaladeJours;

    @Column("frais_relance")
    private BigDecimal fraisRelance;

    @Column("appliquer_frais_automatiquement")
    @Builder.Default
    private Boolean appliquerFraisAutomatiquement = false;

    @Column("created_by")
    private UUID createdBy;

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("updated_at")
    private LocalDateTime updatedAt;
}