package com.yooyob.erp.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("historique_facturation_recurrente")
public class HistoriqueFacturationRecurrente {

    @PrimaryKey
    @Column("id_historique")
    private UUID idHistorique;

    @NotNull(message = "L'ID abonnement est obligatoire")
    @Column("id_abonnement")
    private UUID idAbonnement;

    @Column("nom_abonnement")
    private String nomAbonnement;

    @Column("id_facture_generee")
    private UUID idFactureGeneree;

    @Column("numero_facture_generee")
    private String numeroFactureGeneree;

    @NotNull(message = "La date d'exécution est obligatoire")
    @Column("date_execution")
    private LocalDateTime dateExecution;

    @Column("date_facture")
    private LocalDateTime dateFacture;

    @Column("montant_facture")
    private BigDecimal montantFacture;

    @Column("succes")
    @Builder.Default
    private Boolean succes = false;

    @Column("message_erreur")
    private String messageErreur;

    @Column("details_execution")
    private String detailsExecution;

    @Column("email_envoye")
    @Builder.Default
    private Boolean emailEnvoye = false;

    @Column("pdf_genere")
    @Builder.Default
    private Boolean pdfGenere = false;

    @Column("temps_execution_ms")
    private Long tempsExecutionMs;

    @Column("created_at")
    private LocalDateTime createdAt;
}