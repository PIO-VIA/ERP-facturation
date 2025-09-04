package com.yooyob.erp.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.UserDefinedType;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@UserDefinedType("ligne_taxe")
public class LigneTaxe {

    @Column("id_ligne")
    private UUID idLigne;

    @NotNull(message = "Le type de ratio est obligatoire")
    @Column("type_ratio")
    private String typeRatio;

    @NotNull(message = "Le ratio est obligatoire")
    @Column("ratio")
    private BigDecimal ratio;

    @NotNull(message = "Le montant de base est obligatoire")
    @Column("montant_base")
    private BigDecimal montantBase;

    @NotNull(message = "Le montant de la taxe est obligatoire")
    @Column("montant_taxe")
    private BigDecimal montantTaxe;

    @Column("nom_taxe")
    private String nomTaxe;

    @Column("id_taxe")
    private UUID idTaxe;
}