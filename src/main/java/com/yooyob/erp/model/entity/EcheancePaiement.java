package com.yooyob.erp.model.entity;

import com.yooyob.erp.model.enums.TypeEcheance;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

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
@Table("echeances_paiement")
public class EcheancePaiement {

    @PrimaryKey
    @Column("id_echeance")
    private UUID idEcheance;

    @NotNull(message = "L'ID de la facture est obligatoire")
    @Column("id_facture")
    private UUID idFacture;

    @Column("numero_facture")
    private String numeroFacture;

    @NotNull(message = "Le type d'échéance est obligatoire")
    @Column("type_echeance")
    private TypeEcheance typeEcheance;

    @Column("numero_echeance")
    private Integer numeroEcheance;

    @NotNull(message = "Le montant est obligatoire")
    @PositiveOrZero(message = "Le montant doit être positif ou nul")
    @Column("montant_echeance")
    private BigDecimal montantEcheance;

    @Column("pourcentage_facture")
    private BigDecimal pourcentageFacture;

    @NotNull(message = "La date d'échéance est obligatoire")
    @Column("date_echeance")
    private LocalDate dateEcheance;

    @Column("date_paiement")
    private LocalDate datePaiement;

    @Column("montant_paye")
    @Builder.Default
    private BigDecimal montantPaye = BigDecimal.ZERO;

    @Column("montant_restant")
    private BigDecimal montantRestant;

    @Column("statut")
    @Builder.Default
    private String statut = "EN_ATTENTE"; // EN_ATTENTE, PAYE, PARTIEL, RETARD

    @Column("jours_retard")
    @Builder.Default
    private Integer joursRetard = 0;

    @Column("penalites_retard")
    @Builder.Default
    private BigDecimal penalitesRetard = BigDecimal.ZERO;

    @Column("taux_penalite")
    private BigDecimal tauxPenalite;

    @Column("escomptes_applicables")
    private List<EscomptePaiement> escomptesApplicables;

    @Column("escompte_applique")
    private UUID escompteApplique;

    @Column("montant_escompte")
    @Builder.Default
    private BigDecimal montantEscompte = BigDecimal.ZERO;

    @Column("conditions_paiement")
    private String conditionsPaiement;

    @Column("mode_paiement_suggere")
    private String modePaiementSuggere;

    @Column("reference_paiement")
    private String referencePaiement;

    @Column("notes")
    private String notes;

    @Column("rappels_envoyes")
    @Builder.Default
    private Integer rappelsEnvoyes = 0;

    @Column("derniere_relance")
    private LocalDateTime derniereRelance;

    @Column("prochaine_relance")
    private LocalDateTime prochaineRelance;

    @Column("blocage_client")
    @Builder.Default
    private Boolean blocageClient = false;

    @Column("date_blocage")
    private LocalDateTime dateBlocage;

    @Column("motif_blocage")
    private String motifBlocage;

    @Column("priorite")
    @Builder.Default
    private Integer priorite = 1;

    @Column("devise")
    private String devise;

    @Column("taux_change")
    @Builder.Default
    private BigDecimal tauxChange = BigDecimal.ONE;

    @Column("montant_devise_origine")
    private BigDecimal montantDeviseOrigine;

    @Column("conditions_personnalisees")
    private String conditionsPersonnalisees;

    @Column("validation_requise")
    @Builder.Default
    private Boolean validationRequise = false;

    @Column("valide_par")
    private UUID validePar;

    @Column("date_validation")
    private LocalDateTime dateValidation;

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("updated_at")
    private LocalDateTime updatedAt;
}