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
@Table("devises")
public class Devise {

    @PrimaryKey
    @Column("id_devise")
    private UUID idDevise;

    @NotBlank(message = "Le nom de la devise est obligatoire")
    @Column("nom_devise")
    private String nomDevise;

    @NotBlank(message = "Le symbole est obligatoire")
    @Column("symbole")
    private String symbole;

    @NotBlank(message = "Le code ISO est obligatoire")
    @Column("code_iso")
    private String codeIso;

    @Column("actif")
    @Builder.Default
    private Boolean actif = true;

    @Column("devise_base")
    @Builder.Default
    private Boolean deviseBase = false;

    @Column("taux_change_actuel")
    @Builder.Default
    private BigDecimal tauxChangeActuel = BigDecimal.ONE;

    @Column("derniere_mise_a_jour_taux")
    private LocalDateTime derniereMiseAJourTaux;

    @Column("source_taux_auto")
    private String sourceTauxAuto;

    @Column("precision_decimale")
    @Builder.Default
    private Integer precisionDecimale = 2;

    @Column("position_symbole")
    @Builder.Default
    private String positionSymbole = "AVANT"; // AVANT, APRES

    @Column("separateur_milliers")
    @Builder.Default
    private String separateurMilliers = " ";

    @Column("separateur_decimal")
    @Builder.Default
    private String separateurDecimal = ",";

    @Column("unite_monetaire")
    private String uniteMonetaire;

    @Column("sous_unite_monetaire")
    private String sousUniteMonetaire;

    @NotNull(message = "Le facteur de conversion est obligatoire")
    @Positive(message = "Le facteur de conversion doit être positif")
    @Column("facteur_conversion")
    private BigDecimal facteurConversion;

    @Column("nom_mesure")
    private String nomMesure;

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("updated_at")
    private LocalDateTime updatedAt;
}