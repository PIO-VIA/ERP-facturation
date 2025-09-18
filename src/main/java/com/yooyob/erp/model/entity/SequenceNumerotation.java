package com.yooyob.erp.model.entity;

import com.yooyob.erp.model.enums.TypeNumerotation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("sequences_numerotation")
public class SequenceNumerotation {

    @PrimaryKey
    @Column("id_sequence")
    private UUID idSequence;

    @NotNull(message = "L'ID de configuration est obligatoire")
    @Column("id_configuration")
    private UUID idConfiguration;

    @NotNull(message = "Le type de numérotation est obligatoire")
    @Column("type_numerotation")
    private TypeNumerotation typeNumerotation;

    @Column("cle_sequence")
    private String cleSequence; // ex: "FACTURE-2024" pour reset annuel

    @Column("valeur_courante")
    @Builder.Default
    private Long valeurCourante = 0L;

    @Column("valeur_precedente")
    private Long valeurPrecedente;

    @Column("derniere_utilisation")
    private LocalDateTime derniereUtilisation;

    @Column("derniere_valeur_generee")
    private String derniereValeurGeneree;

    @Column("nombre_utilisations")
    @Builder.Default
    private Long nombreUtilisations = 0L;

    @Column("verrouille")
    @Builder.Default
    private Boolean verrouille = false;

    @Column("motif_verrouillage")
    private String motifVerrouillage;

    @Column("date_reset")
    private LocalDateTime dateReset;

    @Column("prochaine_date_reset")
    private LocalDateTime prochaineDateReset;

    @Column("periode_courante")
    private String periodeCourante; // "2024", "2024-01", etc.

    @Column("site")
    private String site;

    @Column("departement")
    private String departement;

    @Column("metadata")
    private String metadata;

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("updated_at")
    private LocalDateTime updatedAt;
}