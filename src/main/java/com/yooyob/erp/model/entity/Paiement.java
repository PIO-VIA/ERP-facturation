package com.yooyob.erp.model.entity;

import com.yooyob.erp.model.enums.TypePaiement;
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
@Table("paiements")
public class Paiement {

    @PrimaryKey
    @Column("id_paiement")
    private UUID idPaiement;

    @NotNull(message = "L'ID client est obligatoire")
    @Column("id_client")
    private UUID idClient;

    @NotNull(message = "Le montant est obligatoire")
    @Positive(message = "Le montant doit être positif")
    @Column("montant")
    private BigDecimal montant;

    @NotNull(message = "La date est obligatoire")
    @Column("date")
    private LocalDate date;

    @NotNull(message = "Le journal est obligatoire")
    @Column("journal")
    private String journal;

    @NotNull(message = "Le mode de paiement est obligatoire")
    @Column("mode_paiement")
    private TypePaiement modePaiement;

    @Column("compte_bancaire_f")
    private String compteBancaireF;

    @Column("memo")
    private String memo;

    @Column("id_facture")
    private UUID idFacture;

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("updated_at")
    private LocalDateTime updatedAt;
}
