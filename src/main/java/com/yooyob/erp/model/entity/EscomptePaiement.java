package com.yooyob.erp.model.entity;

import com.yooyob.erp.model.enums.TypeEscompte;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("escomptes_paiement")
public class EscomptePaiement {

    @PrimaryKey
    @Column("id_escompte")
    private UUID idEscompte;

    @NotBlank(message = "Le nom de l'escompte est obligatoire")
    @Column("nom_escompte")
    private String nomEscompte;

    @Column("description")
    private String description;

    @NotNull(message = "Le type d'escompte est obligatoire")
    @Column("type_escompte")
    private TypeEscompte typeEscompte;

    @Column("code_escompte")
    private String codeEscompte;

    @PositiveOrZero(message = "Le pourcentage doit être positif ou nul")
    @Column("pourcentage_escompte")
    private BigDecimal pourcentageEscompte;

    @PositiveOrZero(message = "Le montant fixe doit être positif ou nul")
    @Column("montant_fixe")
    private BigDecimal montantFixe;

    @Column("montant_minimum")
    private BigDecimal montantMinimum;

    @Column("montant_maximum")
    private BigDecimal montantMaximum;

    @Column("date_debut_validite")
    private LocalDate dateDebutValidite;

    @Column("date_fin_validite")
    private LocalDate dateFinValidite;

    @Column("jours_paiement_anticipe")
    private Integer joursPaiementAnticipe;

    @Column("jours_maximum_anticipe")
    private Integer joursMaximumAnticipe;

    @Column("cumul_possible")
    @Builder.Default
    private Boolean cumulPossible = false;

    @Column("escomptes_exclus")
    private List<UUID> escomptesExclus;

    @Column("clients_eligibles")
    private List<UUID> clientsEligibles;

    @Column("types_factures")
    private List<String> typesFactures;

    @Column("produits_eligibles")
    private List<UUID> produitsEligibles;

    @Column("categories_eligibles")
    private List<String> categoriesEligibles;

    @Column("montant_commande_minimum")
    private BigDecimal montantCommandeMinimum;

    @Column("quantite_minimum")
    private Integer quantiteMinimum;

    @Column("premiere_commande_uniquement")
    @Builder.Default
    private Boolean premiereCommandeUniquement = false;

    @Column("frequence_utilisation_max")
    private Integer frequenceUtilisationMax;

    @Column("nombre_utilisations")
    @Builder.Default
    private Integer nombreUtilisations = 0;

    @Column("nombre_max_utilisations")
    private Integer nombreMaxUtilisations;

    @Column("budget_total_escompte")
    private BigDecimal budgetTotalEscompte;

    @Column("budget_utilise")
    @Builder.Default
    private BigDecimal budgetUtilise = BigDecimal.ZERO;

    @Column("actif")
    @Builder.Default
    private Boolean actif = true;

    @Column("automatique")
    @Builder.Default
    private Boolean automatique = false;

    @Column("code_promo_requis")
    @Builder.Default
    private Boolean codePromoRequis = false;

    @Column("validation_manuelle")
    @Builder.Default
    private Boolean validationManuelle = false;

    @Column("conditions_particulieres")
    private String conditionsParticulieres;

    @Column("message_client")
    private String messageClient;

    @Column("priorite")
    @Builder.Default
    private Integer priorite = 1;

    @Column("groupe_escomptes")
    private String groupeEscomptes;

    @Column("tags")
    private List<String> tags;

    @Column("statistiques_utilisation")
    private String statistiquesUtilisation;

    @Column("created_by")
    private UUID createdBy;

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("updated_at")
    private LocalDateTime updatedAt;
}