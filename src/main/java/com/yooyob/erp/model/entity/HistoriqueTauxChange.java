package com.yooyob.erp.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("historique_taux_change")
public class HistoriqueTauxChange {

    @PrimaryKey
    @Column("id_historique")
    private UUID idHistorique;

    @NotBlank(message = "La devise source est obligatoire")
    @Column("devise_source")
    private String deviseSource;

    @NotBlank(message = "La devise cible est obligatoire")
    @Column("devise_cible")
    private String deviseCible;

    @NotNull(message = "Le taux de change est obligatoire")
    @Positive(message = "Le taux de change doit être positif")
    @Column("taux_change")
    private BigDecimal tauxChange;

    @NotNull(message = "La date d'application est obligatoire")
    @Column("date_application")
    private LocalDateTime dateApplication;

    @Column("date_fin_validite")
    private LocalDateTime dateFinValidite;

    @Column("source_taux")
    private String sourceTaux; // "BANQUE_CENTRALE", "API_EXTERNE", "MANUEL", etc.

    @Column("taux_achat")
    private BigDecimal tauxAchat;

    @Column("taux_vente")
    private BigDecimal tauxVente;

    @Column("taux_moyen")
    private BigDecimal tauxMoyen;

    @Column("spread")
    private BigDecimal spread;

    @Column("commission_pourcentage")
    private BigDecimal commissionPourcentage;

    @Column("commission_fixe")
    private BigDecimal commissionFixe;

    @Column("actif")
    @Builder.Default
    private Boolean actif = true;

    @Column("automatique")
    @Builder.Default
    private Boolean automatique = false;

    @Column("reference_externe")
    private String referenceExterne;

    @Column("metadata")
    private String metadata;

    @Column("created_by")
    private UUID createdBy;

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("updated_at")
    private LocalDateTime updatedAt;
}