package com.yooyob.erp.model.entity;

import com.yooyob.erp.model.enums.TypeWorkflow;
import com.yooyob.erp.model.enums.StatutApprobation;
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
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("demandes_approbation")
public class DemandeApprobation {

    @PrimaryKey
    @Column("id_demande")
    private UUID idDemande;

    @NotNull(message = "L'ID du workflow est obligatoire")
    @Column("id_workflow")
    private UUID idWorkflow;

    @NotNull(message = "Le type de workflow est obligatoire")
    @Column("type_workflow")
    private TypeWorkflow typeWorkflow;

    @Column("titre_demande")
    private String titreDemande;

    @Column("description")
    private String description;

    @NotNull(message = "L'ID de l'objet est obligatoire")
    @Column("id_objet")
    private UUID idObjet;

    @Column("type_objet")
    private String typeObjet; // "FACTURE", "DEVIS", "AVOIR", etc.

    @Column("donnees_objet")
    private Map<String, Object> donneesObjet;

    @Column("montant_concerne")
    private BigDecimal montantConcerne;

    @NotNull(message = "Le demandeur est obligatoire")
    @Column("demandeur")
    private UUID demandeur;

    @Column("nom_demandeur")
    private String nomDemandeur;

    @Column("statut")
    private StatutApprobation statut;

    @Column("etape_courante")
    private Integer etapeCourante;

    @Column("historique_approbations")
    private List<HistoriqueApprobation> historiqueApprobations;

    @Column("approbateurs_en_attente")
    private List<UUID> approbateursEnAttente;

    @Column("date_creation")
    private LocalDateTime dateCreation;

    @Column("date_expiration")
    private LocalDateTime dateExpiration;

    @Column("date_derniere_action")
    private LocalDateTime dateDerniereAction;

    @Column("date_finalisation")
    private LocalDateTime dateFinalisation;

    @Column("commentaires_demandeur")
    private String commentairesDemandeur;

    @Column("commentaires_finaux")
    private String commentairesFinaux;

    @Column("motif_rejet")
    private String motifRejet;

    @Column("notifications_envoyees")
    private List<String> notificationsEnvoyees;

    @Column("escalades_effectuees")
    @Builder.Default
    private Integer escaladeesEffectuees = 0;

    @Column("priorite")
    @Builder.Default
    private Integer priorite = 1;

    @Column("tags")
    private List<String> tags;

    @Column("metadata")
    private Map<String, String> metadata;

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("updated_at")
    private LocalDateTime updatedAt;
}