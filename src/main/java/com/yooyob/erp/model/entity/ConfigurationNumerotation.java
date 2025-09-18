package com.yooyob.erp.model.entity;

import com.yooyob.erp.model.enums.TypeNumerotation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("configurations_numerotation")
public class ConfigurationNumerotation {

    @PrimaryKey
    @Column("id_configuration")
    private UUID idConfiguration;

    @NotNull(message = "Le type de numérotation est obligatoire")
    @Column("type_numerotation")
    private TypeNumerotation typeNumerotation;

    @NotBlank(message = "Le nom de la configuration est obligatoire")
    @Column("nom_configuration")
    private String nomConfiguration;

    @Column("description")
    private String description;

    @NotBlank(message = "Le modèle de numérotation est obligatoire")
    @Column("modele_numerotation")
    private String modeleNumerotation; // ex: "{PREFIX}-{YYYY}-{MM}-{SEQUENCE:5}"

    @Column("prefixe")
    private String prefixe;

    @Column("suffixe")
    private String suffixe;

    @Column("separateur")
    @Builder.Default
    private String separateur = "-";

    @Column("longueur_sequence")
    @Builder.Default
    private Integer longueurSequence = 5;

    @Column("valeur_initiale")
    @Builder.Default
    private Long valeurInitiale = 1L;

    @Column("increment")
    @Builder.Default
    private Long increment = 1L;

    @Column("reset_periodique")
    @Builder.Default
    private Boolean resetPeriodique = false;

    @Column("periode_reset")
    private String periodeReset; // "ANNEE", "MOIS", "JOUR"

    @Column("inclure_annee")
    @Builder.Default
    private Boolean inclureAnnee = true;

    @Column("format_annee")
    @Builder.Default
    private String formatAnnee = "yyyy"; // "yyyy", "yy"

    @Column("inclure_mois")
    @Builder.Default
    private Boolean inclureMois = false;

    @Column("format_mois")
    @Builder.Default
    private String formatMois = "MM"; // "MM", "M"

    @Column("inclure_jour")
    @Builder.Default
    private Boolean inclureJour = false;

    @Column("format_jour")
    @Builder.Default
    private String formatJour = "dd"; // "dd", "d"

    @Column("variables_personnalisees")
    private Map<String, String> variablesPersonnalisees;

    @Column("conditions_application")
    private String conditionsApplication; // expressions pour définir quand appliquer cette config

    @Column("actif")
    @Builder.Default
    private Boolean actif = true;

    @Column("par_defaut")
    @Builder.Default
    private Boolean parDefaut = false;

    @Column("priorite")
    @Builder.Default
    private Integer priorite = 1;

    @Column("site")
    private String site; // pour multi-sites

    @Column("departement")
    private String departement;

    @Column("utilisateur_specifique")
    private UUID utilisateurSpecifique;

    @Column("date_debut_validite")
    private LocalDateTime dateDebutValidite;

    @Column("date_fin_validite")
    private LocalDateTime dateFinValidite;

    @Column("test_mode")
    @Builder.Default
    private Boolean testMode = false;

    @Column("exemples_generes")
    private String exemplesGeneres;

    @Column("derniere_sequence_utilisee")
    @Builder.Default
    private Long derniereSequenceUtilisee = 0L;

    @Column("derniere_utilisation")
    private LocalDateTime derniereUtilisation;

    @Column("compteur_utilisation")
    @Builder.Default
    private Long compteurUtilisation = 0L;

    @Column("created_by")
    private UUID createdBy;

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("updated_at")
    private LocalDateTime updatedAt;
}