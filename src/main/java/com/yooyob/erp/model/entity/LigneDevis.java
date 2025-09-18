package com.yooyob.erp.model.entity;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.mapping.Column;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.cassandra.core.mapping.UserDefinedType;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@UserDefinedType("ligne_devis")
public class LigneDevis {

    @Column("id_ligne")
    private UUID idLigne;

    @NotNull(message = "La quantité est obligatoire")
    @Positive(message = "La quantité doit être positive")
    @Column("quantite")
    private Integer quantite;

    @Column("description")
    private String description;

    @NotNull(message = "Le débit est obligatoire")
    @Column("debit")
    private BigDecimal debit;

    @NotNull(message = "Le crédit est obligatoire")
    @Column("credit")
    private BigDecimal credit;

    @Column("is_tax_line")
    @Builder.Default
    private Boolean isTaxLine = false;

    @Column("id_produit")
    private UUID idProduit;

    @Column("nom_produit")
    private String nomProduit;

    @Column("prix_unitaire")
    private BigDecimal prixUnitaire;

    @Column("montant_total")
    private BigDecimal montantTotal;

    @Column("remise_pourcentage")
    @Builder.Default
    private BigDecimal remisePourcentage = BigDecimal.ZERO;

    @Column("remise_montant")
    @Builder.Default
    private BigDecimal remiseMontant = BigDecimal.ZERO;
}