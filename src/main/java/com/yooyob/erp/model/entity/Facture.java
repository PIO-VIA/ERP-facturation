package com.yooyob.erp.model.entity;

import com.yooyob.erp.model.enums.StatutFacture;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;
import org.springframework.data.cassandra.core.mapping.UserDefinedType;

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
@Table("factures")
public class Facture {

    @PrimaryKey
    @Column("id_facture")
    private UUID idFacture;

    @NotBlank(message = "Le numéro de facture est obligatoire")
    @Column("numero_facture")
    private String numeroFacture;

    @NotNull(message = "La date de facturation est obligatoire")
    @Column("date_facturation")
    private LocalDate dateFacturation;

    @NotNull(message = "La date d'échéance est obligatoire")
    @Column("date_echeance")
    private LocalDate dateEcheance;

    @Column("type")
    private String type;

    @NotNull(message = "L'état est obligatoire")
    @Column("etat")
    private StatutFacture etat;

    @NotNull(message = "Le montant total est obligatoire")
    @PositiveOrZero(message = "Le montant total doit être positif ou nul")
    @Column("montant_total")
    private BigDecimal montantTotal;

    @NotNull(message = "Le montant restant est obligatoire")
    @PositiveOrZero(message = "Le montant restant doit être positif ou nul")
    @Column("montant_restant")
    private BigDecimal montantRestant;

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

    @Column("lignes_facture")
    private List<LigneFacture> lignesFacture;

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

    @Column("reference_commande")
    private String referenceCommande;

    @Column("pdf_path")
    private String pdfPath;

    @Column("envoye_par_email")
    @Builder.Default
    private Boolean envoyeParEmail = false;

    @Column("date_envoi_email")
    private LocalDateTime dateEnvoiEmail;

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("updated_at")
    private LocalDateTime updatedAt;
}