package com.yooyob.erp.model.entity;

import com.yooyob.erp.model.enums.StatutApprobation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.UserDefinedType;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@UserDefinedType("historique_approbation")
public class HistoriqueApprobation {

    @Column("ordre_action")
    private Integer ordreAction;

    @Column("etape")
    private Integer etape;

    @Column("nom_etape")
    private String nomEtape;

    @Column("approbateur")
    private UUID approbateur;

    @Column("nom_approbateur")
    private String nomApprobateur;

    @Column("action")
    private StatutApprobation action;

    @Column("commentaires")
    private String commentaires;

    @Column("date_action")
    private LocalDateTime dateAction;

    @Column("delai_reponse_heures")
    private Long delaiReponseHeures;

    @Column("escalade")
    @Builder.Default
    private Boolean escalade = false;

    @Column("escalade_depuis")
    private UUID escaladeDepuis;

    @Column("ip_adresse")
    private String ipAdresse;

    @Column("metadata")
    private String metadata;
}