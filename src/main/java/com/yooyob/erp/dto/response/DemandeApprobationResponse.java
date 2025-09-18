package com.yooyob.erp.dto.response;

import com.yooyob.erp.model.enums.StatutApprobation;
import com.yooyob.erp.model.enums.TypeWorkflow;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DemandeApprobationResponse {

    private UUID idDemande;
    private UUID idWorkflow;
    private TypeWorkflow typeWorkflow;
    private String titreDemande;
    private String description;
    private UUID idObjet;
    private String typeObjet;
    private Map<String, Object> donneesObjet;
    private BigDecimal montantConcerne;
    private UUID demandeur;
    private String nomDemandeur;
    private StatutApprobation statut;
    private Integer etapeCourante;
    private List<HistoriqueApprobationResponse> historiqueApprobations;
    private List<UUID> approbateursEnAttente;
    private LocalDateTime dateCreation;
    private LocalDateTime dateExpiration;
    private LocalDateTime dateDerniereAction;
    private LocalDateTime dateFinalisation;
    private String commentairesDemandeur;
    private String commentairesFinaux;
    private String motifRejet;
    private List<String> notificationsEnvoyees;
    private Integer escaladeesEffectuees;
    private Integer priorite;
    private List<String> tags;
    private Map<String, String> metadata;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}