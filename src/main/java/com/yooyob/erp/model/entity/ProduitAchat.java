package com.yooyob.erp.model.entity;

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
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("produits_achat")
public class ProduitAchat {

    @PrimaryKey
    @Column("id_produit")
    private UUID idProduit;

    @NotBlank(message = "Le nom du produit est obligatoire")
    @Column("nom_produit")
    private String nomProduit;

    @Column("type_produit")
    private String typeProduit;

    @NotNull(message = "Le prix d'achat est obligatoire")
    @PositiveOrZero(message = "Le prix d'achat doit être positif ou nul")
    @Column("prix_achat")
    private BigDecimal prixAchat;

    @Column("cout_standard")
    private BigDecimal coutStandard;

    @Column("categorie")
    private String categorie;

    @Column("reference")
    private String reference;

    @Column("code_barre")
    private String codeBarre;

    @Column("description")
    private String description;

    @Column("fournisseur_principal")
    private UUID fournisseurPrincipal;

    @Column("stock_minimum")
    @Builder.Default
    private Integer stockMinimum = 0;

    @Column("stock_actuel")
    @Builder.Default
    private Integer stockActuel = 0;

    @Column("active")
    @Builder.Default
    private Boolean active = true;

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("updated_at")
    private LocalDateTime updatedAt;
}