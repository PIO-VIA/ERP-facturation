package com.yooyob.erp.model.entity;

import com.yooyob.erp.model.enums.FrequenceRecurrence;
import com.yooyob.erp.model.enums.StatutAbonnement;
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
@Table("abonnements_facturation")
public class AbonnementFacturation {

    @PrimaryKey
    @Column("id_abonnement")
    private UUID idAbonnement;

    @NotBlank(message = "Le nom de l'abonnement est obligatoire")
    @Column("nom_abonnement")
    private String nomAbonnement;

    @Column("description")
    private String description;

    @NotNull(message = "L'ID client est obligatoire")
    @Column("id_client")
    private UUID idClient;

    @Column("nom_client")
    private String nomClient;

    @Column("email_client")
    private String emailClient;

    @NotNull(message = "Le statut est obligatoire")
    @Column("statut")
    private StatutAbonnement statut;

    @NotNull(message = "La fréquence de récurrence est obligatoire")
    @Column("frequence_recurrence")
    private FrequenceRecurrence frequenceRecurrence;

    @Column("jour_facturation")
    private Integer jourFacturation; // jour du mois (1-31) ou jour de la semaine (1-7) selon fréquence

    @NotNull(message = "La date de début est obligatoire")
    @Column("date_debut")
    private LocalDate dateDebut;

    @Column("date_fin")
    private LocalDate dateFin;

    @Column("date_prochaine_facturation")
    private LocalDate dateProchaineFacturation;

    @Column("date_derniere_facturation")
    private LocalDate dateDerniereFacturation;

    @NotNull(message = "Le montant est obligatoire")
    @PositiveOrZero(message = "Le montant doit être positif ou nul")
    @Column("montant_recurrent")
    private BigDecimal montantRecurrent;

    @Column("lignes_template")
    private List<LigneFacture> lignesTemplate;

    @Column("devise")
    private String devise;

    @Column("taux_change")
    @Builder.Default
    private BigDecimal tauxChange = BigDecimal.ONE;

    @Column("conditions_paiement")
    private String conditionsPaiement;

    @Column("notes_template")
    private String notesTemplate;

    @Column("nombre_factures_generees")
    @Builder.Default
    private Integer nombreFacturesGenerees = 0;

    @Column("nombre_max_factures")
    private Integer nombreMaxFactures; // null = illimité

    @Column("montant_total_facture")
    @Builder.Default
    private BigDecimal montantTotalFacture = BigDecimal.ZERO;

    @Column("auto_envoyer_email")
    @Builder.Default
    private Boolean autoEnvoyerEmail = false;

    @Column("auto_generer_pdf")
    @Builder.Default
    private Boolean autoGenererPdf = true;

    @Column("jours_avant_rappel")
    @Builder.Default
    private Integer joursAvantRappel = 3;

    @Column("template_email_personnalise")
    private String templateEmailPersonnalise;

    @Column("actif")
    @Builder.Default
    private Boolean actif = true;

    @Column("derniere_erreur")
    private String derniereErreur;

    @Column("date_derniere_erreur")
    private LocalDateTime dateDerniereErreur;

    @Column("created_by")
    private UUID createdBy;

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("updated_at")
    private LocalDateTime updatedAt;
}