package com.yooyob.erp.model.entity;

import com.yooyob.erp.model.enums.StatutDevis;
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
@Table("devis")
public class Devis {

    @PrimaryKey
    @Column("id_devis")
    private UUID idDevis;

    @NotBlank(message = "Le numéro de devis est obligatoire")
    @Column("numero_devis")
    private String numeroDevis;

    @NotNull(message = "La date de création est obligatoire")
    @Column("date_creation")
    private LocalDate dateCreation;

    @NotNull(message = "La date de validité est obligatoire")
    @Column("date_validite")
    private LocalDate dateValidite;

    @Column("type")
    private String type;

    @NotNull(message = "Le statut est obligatoire")
    @Column("statut")
    private StatutDevis statut;

    @NotNull(message = "Le montant total est obligatoire")
    @PositiveOrZero(message = "Le montant total doit être positif ou nul")
    @Column("montant_total")
    private BigDecimal montantTotal;

    @NotNull(message = "L'ID client est obligatoire")
    @Column("id_client")
    private UUID idClient;

    @Column("nom_client")
    private String nomClient;

    @Column("adresse_client")
    private String adresseClient;

    @Column("email_client")
    private String emailClient;

    @Column("telephone_client")
    private String telephoneClient;

    @Column("lignes_devis")
    private List<LigneDevis> lignesDevis;

    @Column("montant_ht")
    private BigDecimal montantHT;

    @Column("montant_tva")
    private BigDecimal montantTVA;

    @Column("montant_ttc")
    private BigDecimal montantTTC;

    @Column("devise")
    private String devise;

    @Column("taux_change")
    @Builder.Default
    private BigDecimal tauxChange = BigDecimal.ONE;

    @Column("conditions_paiement")
    private String conditionsPaiement;

    @Column("notes")
    private String notes;

    @Column("reference_externe")
    private String referenceExterne;

    @Column("pdf_path")
    private String pdfPath;

    @Column("envoye_par_email")
    @Builder.Default
    private Boolean envoyeParEmail = false;

    @Column("date_envoi_email")
    private LocalDateTime dateEnvoiEmail;

    @Column("date_acceptation")
    private LocalDateTime dateAcceptation;

    @Column("date_refus")
    private LocalDateTime dateRefus;

    @Column("motif_refus")
    private String motifRefus;

    @Column("id_facture_convertie")
    private UUID idFactureConvertie;

    @Column("remise_globale_pourcentage")
    @Builder.Default
    private BigDecimal remiseGlobalePourcentage = BigDecimal.ZERO;

    @Column("remise_globale_montant")
    @Builder.Default
    private BigDecimal remiseGlobaleMontant = BigDecimal.ZERO;

    @Column("validite_offre_jours")
    @Builder.Default
    private Integer validiteOffreJours = 30;

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("updated_at")
    private LocalDateTime updatedAt;
}