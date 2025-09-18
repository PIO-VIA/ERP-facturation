package com.yooyob.erp.model.entity;

import com.yooyob.erp.model.enums.StatutAvoir;
import com.yooyob.erp.model.enums.TypeAvoir;
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
@Table("factures_avoir")
public class FactureAvoir {

    @PrimaryKey
    @Column("id_avoir")
    private UUID idAvoir;

    @NotBlank(message = "Le numéro d'avoir est obligatoire")
    @Column("numero_avoir")
    private String numeroAvoir;

    @NotNull(message = "La date de création est obligatoire")
    @Column("date_creation")
    private LocalDate dateCreation;

    @Column("date_validation")
    private LocalDate dateValidation;

    @NotNull(message = "Le type d'avoir est obligatoire")
    @Column("type_avoir")
    private TypeAvoir typeAvoir;

    @NotNull(message = "Le statut est obligatoire")
    @Column("statut")
    private StatutAvoir statut;

    @NotNull(message = "Le montant total est obligatoire")
    @PositiveOrZero(message = "Le montant total doit être positif ou nul")
    @Column("montant_total")
    private BigDecimal montantTotal;

    @NotNull(message = "L'ID de la facture d'origine est obligatoire")
    @Column("id_facture_origine")
    private UUID idFactureOrigine;

    @Column("numero_facture_origine")
    private String numeroFactureOrigine;

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

    @Column("lignes_avoir")
    private List<LigneAvoir> lignesAvoir;

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

    @Column("motif_avoir")
    private String motifAvoir;

    @Column("notes")
    private String notes;

    @Column("pdf_path")
    private String pdfPath;

    @Column("envoye_par_email")
    @Builder.Default
    private Boolean envoyeParEmail = false;

    @Column("date_envoi_email")
    private LocalDateTime dateEnvoiEmail;

    @Column("date_application")
    private LocalDateTime dateApplication;

    @Column("montant_applique")
    @Builder.Default
    private BigDecimal montantApplique = BigDecimal.ZERO;

    @Column("montant_rembourse")
    @Builder.Default
    private BigDecimal montantRembourse = BigDecimal.ZERO;

    @Column("mode_remboursement")
    private String modeRemboursement;

    @Column("reference_remboursement")
    private String referenceRemboursement;

    @Column("date_remboursement")
    private LocalDateTime dateRemboursement;

    @Column("approuve_par")
    private UUID approuvePar;

    @Column("date_approbation")
    private LocalDateTime dateApprobation;

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("updated_at")
    private LocalDateTime updatedAt;
}