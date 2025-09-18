package com.yooyob.erp.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.UserDefinedType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@UserDefinedType("alerte_tableau")
public class AlerteTableau {

    @Column("id_alerte")
    private UUID idAlerte;

    @Column("nom_alerte")
    private String nomAlerte;

    @Column("description")
    private String description;

    @Column("id_widget")
    private UUID idWidget;

    @Column("metrique_surveillee")
    private String metriqueSurveillee;

    @Column("operateur")
    private String operateur; // "SUPERIEUR", "INFERIEUR", "EGAL", "DIFFERENT"

    @Column("valeur_seuil")
    private BigDecimal valeurSeuil;

    @Column("pourcentage_variation")
    private BigDecimal pourcentageVariation;

    @Column("periode_comparaison")
    private String periodeComparaison; // "JOUR_PRECEDENT", "SEMAINE_PRECEDENTE", etc.

    @Column("frequence_verification")
    private Integer frequenceVerification; // en minutes

    @Column("actif")
    @Builder.Default
    private Boolean actif = true;

    @Column("niveau_criticite")
    private String niveauCriticite; // "INFO", "WARNING", "ERROR", "CRITICAL"

    @Column("destinataires_notification")
    private List<String> destinatairesNotification;

    @Column("canal_notification")
    private List<String> canalNotification; // "EMAIL", "SMS", "PUSH", "SLACK"

    @Column("message_personnalise")
    private String messagePersonnalise;

    @Column("actions_automatiques")
    private List<String> actionsAutomatiques;

    @Column("derniere_verification")
    private LocalDateTime derniereVerification;

    @Column("derniere_alerte")
    private LocalDateTime derniereAlerte;

    @Column("nombre_declenchements")
    @Builder.Default
    private Long nombreDeclenchements = 0L;

    @Column("cooldown_minutes")
    @Builder.Default
    private Integer cooldownMinutes = 60; // éviter spam d'alertes

    @Column("conditions_complementaires")
    private String conditionsComplementaires;
}