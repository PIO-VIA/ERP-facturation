package com.yooyob.erp.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("remboursements")
public class Remboursement {

    @PrimaryKey
    @Column("id_remboursement")
    private UUID idRemboursement;

    @NotNull(message = "La date de facturation est obligatoire")
    @Column("date_facturation")
    private LocalDate dateFacturation;

    @NotNull(message = "La date comptable est obligatoire")
    @Column("date_comptable")
    private LocalDate dateComptable;

    @Column("reference_paiement")
    private String referencePaiement;

    @Column("banque_destination")
    private String banqueDestination;

    @NotNull(message = "La date d'échéance est obligatoire")
    @Column("date_echeance")
    private LocalDate dateEcheance;

    @NotNull(message = "Le montant est obligatoire")
    @Positive(message = "Le montant doit être positif")
    @Column("montant")
    private BigDecimal montant;

    @Column("devise")
    private String devise;

    @Column("taux_change")
    @Builder.Default
    private BigDecimal tauxChange = BigDecimal.ONE;

    @Column("motif")
    private String motif;

    @Column("numero_piece")
    private String numeroPiece;

    @Column("statut")
    @Builder.Default
    private String statut = "EN_ATTENTE";

    @Column("id_facture")
    private UUID idFacture;

    @Column("id_client")
    private UUID idClient;

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("updated_at")
    private LocalDateTime updatedAt;
}