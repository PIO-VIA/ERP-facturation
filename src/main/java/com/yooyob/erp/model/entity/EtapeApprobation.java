package com.yooyob.erp.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.UserDefinedType;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@UserDefinedType("etape_approbation")
public class EtapeApprobation {

    @Column("ordre_etape")
    private Integer ordreEtape;

    @Column("nom_etape")
    private String nomEtape;

    @Column("description")
    private String description;

    @Column("approbateurs_requis")
    private List<UUID> approubateursRequis;

    @Column("roles_approbateurs")
    private List<String> rolesApprobateurs;

    @Column("nombre_approbations_requises")
    @Builder.Default
    private Integer nombreApprobationsRequises = 1;

    @Column("obligatoire")
    @Builder.Default
    private Boolean obligatoire = true;

    @Column("parallele")
    @Builder.Default
    private Boolean parallele = false;

    @Column("conditions_passage")
    private String conditionsPassage;

    @Column("delai_max_heures")
    private Integer delaiMaxHeures;

    @Column("escalade_vers")
    private List<UUID> escaladeVers;

    @Column("actif")
    @Builder.Default
    private Boolean actif = true;
}